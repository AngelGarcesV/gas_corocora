# Implementaciones de Service Task en Camunda

Este documento describe las diferentes formas de implementar Service Tasks en el proceso BPMN de Gas Corocora.

## 1. Java Class (camunda:class)

**Implementación:** `ActivarServicioDelegate.java`

```xml
<bpmn:serviceTask id="Activity_03ums6g" 
                  name="Notificar instalación exitosa" 
                  camunda:class="com.example.demo.delegate.ActivarServicioDelegate">
</bpmn:serviceTask>
```

**Características:**
- Implementa la interfaz `JavaDelegate`
- Se instancia cada vez que se ejecuta (no es singleton)
- Nombre completo de la clase (FQCN) en el atributo `camunda:class`
- **Uso:** Activación del servicio de gas y registro en base de datos

**Código:**
```java
@Component("activarServicioDelegate")
public class ActivarServicioDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Lógica de activación
        String businessKey = execution.getBusinessKey();
        // Llamada a transactional-service vía REST
        restTemplate.postForObject(url, null, Map.class);
    }
}
```

## 2. Delegate Expression (camunda:delegateExpression)

**Implementación:** `RegistrarClienteDelegate.java`, `RegistrarPagoDelegate.java`

```xml
<bpmn:serviceTask id="Activity_RegistrarCliente" 
                  name="Registrar cliente" 
                  camunda:delegateExpression="#{registrarClienteDelegate}">
</bpmn:serviceTask>
```

**Características:**
- Usa Spring Expression Language (SpEL) con `#{nombreBean}`
- Spring gestiona el bean (singleton por defecto)
- Mayor flexibilidad y reutilización
- **Uso:** Conexión con transactional-service para persistencia

**Ejemplos:**

### RegistrarClienteDelegate
```java
@Component("registrarClienteDelegate")
public class RegistrarClienteDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // Obtener variables
        String cedula = (String) execution.getVariable("cedula");
        
        // Construir DTO
        Map<String, Object> clienteDTO = new HashMap<>();
        clienteDTO.put("cedula", cedula);
        
        // Llamar API REST
        String url = transactionalServiceUrl + "/api/clientes";
        restTemplate.postForObject(url, request, Map.class);
    }
}
```

### RegistrarPagoDelegate
```java
@Component("registrarPagoDelegate")
public class RegistrarPagoDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // Registrar pago en base de datos transaccional
        String url = transactionalServiceUrl + "/api/facturacion/pago";
        restTemplate.postForObject(url, facturacionDTO, Map.class);
    }
}
```

## 3. Expression - Cálculo Aritmético (camunda:expression)

**Implementación:** `CalculadoraService.java`

```xml
<bpmn:serviceTask id="Task_CalcularDescuento" 
                  name="Calcular descuento" 
                  camunda:expression="${calculadoraService.calcularDescuento(estrato, conexion)}"
                  camunda:resultVariable="descuento">
</bpmn:serviceTask>
```

**Características:**
- Evalúa expresión SpEL directamente
- Puede hacer cálculos aritméticos básicos
- Resultado guardado en `camunda:resultVariable`
- No requiere implementar `JavaDelegate`

**Ejemplos de expresiones:**

### Cálculo aritmético simple
```xml
<!-- Suma simple -->
<camunda:expression>${monto1 + monto2}</camunda:expression>

<!-- Resta -->
<camunda:expression>${conexion - descuento}</camunda:expression>

<!-- Multiplicación -->
<camunda:expression>${precio * cantidad}</camunda:expression>

<!-- División -->
<camunda:expression>${total / 2}</camunda:expression>
```

### Operaciones condicionales
```xml
<camunda:expression>${estrato <= 3 ? 0.30 : 0.10}</camunda:expression>
```

## 4. Expression - Método Público (camunda:expression)

**Implementación:** `CalculadoraService.java`

```xml
<bpmn:serviceTask id="Task_CalcularDescuento" 
                  name="Calcular descuento según estrato" 
                  camunda:expression="${calculadoraService.calcularDescuento(estrato, conexion)}"
                  camunda:resultVariable="descuento">
</bpmn:serviceTask>
```

**Características:**
- Llama método público de un bean de Spring
- Bean debe estar anotado con `@Component`
- Método NO implementa `JavaDelegate`
- Más simple que delegate para lógica de negocio

**Código:**
```java
@Component("calculadoraService")
public class CalculadoraService {
    
    public double calcularDescuento(Integer estrato, Double conexion) {
        double porcentaje = switch (estrato) {
            case 1 -> 0.40; // 40%
            case 2 -> 0.30; // 30%
            case 3 -> 0.20; // 20%
            default -> 0.0;
        };
        return conexion * porcentaje;
    }
    
    public double calcularMontoFinal(Double conexion, Double descuento) {
        return conexion - descuento;
    }
}
```

**Uso en BPMN:**
```xml
<!-- Calcular descuento -->
<bpmn:serviceTask id="Task_1" 
                  camunda:expression="${calculadoraService.calcularDescuento(estrato, conexion)}"
                  camunda:resultVariable="descuento" />

<!-- Calcular monto final -->
<bpmn:serviceTask id="Task_2" 
                  camunda:expression="${calculadoraService.calcularMontoFinal(conexion, descuento)}"
                  camunda:resultVariable="conexion_a_pagar" />
```

## 5. Connector - Llamada a API Externa

**Implementación:** Camunda HTTP Connector

```xml
<bpmn:serviceTask id="Activity_RegistrarCliente" 
                  name="Registrar cliente en BD">
  <bpmn:extensionElements>
    <camunda:connector>
      <camunda:inputOutput>
        <camunda:inputParameter name="url">http://localhost:8083/api/clientes</camunda:inputParameter>
        <camunda:inputParameter name="method">POST</camunda:inputParameter>
        <camunda:inputParameter name="headers">
          <camunda:map>
            <camunda:entry key="Content-Type">application/json</camunda:entry>
          </camunda:map>
        </camunda:inputParameter>
        <camunda:inputParameter name="payload">
          <camunda:script scriptFormat="JavaScript"><![CDATA[
var cliente = {
  "cedula": execution.getVariable("cedula"),
  "nombre": execution.getVariable("nombre"),
  "apellido": execution.getVariable("apellido"),
  "direccion": execution.getVariable("direccion"),
  "ciudad": execution.getVariable("ciudad"),
  "estrato": execution.getVariable("estrato"),
  "telefono": execution.getVariable("telefono"),
  "email": execution.getVariable("email")
};
JSON.stringify(cliente);
          ]]></camunda:script>
        </camunda:inputParameter>
        <camunda:outputParameter name="clienteId">
          <camunda:script scriptFormat="JavaScript"><![CDATA[
var response = connector.getVariable("response");
var json = S(response);
json.prop("id").numberValue();
          ]]></camunda:script>
        </camunda:outputParameter>
      </camunda:inputOutput>
      <camunda:connectorId>http-connector</camunda:connectorId>
    </camunda:connector>
  </bpmn:extensionElements>
</bpmn:serviceTask>
```

**Características:**
- Usa el conector HTTP nativo de Camunda
- Configuración declarativa en XML
- No requiere código Java adicional
- Integración directa con APIs REST externas
- Manejo de entrada/salida con scripts JavaScript

**Arquitectura:**
```
┌──────────────┐         ┌─────────────────┐         ┌────────────────────┐
│ BPMN Process │ ──────> │  HTTP Connector │ ──────> │ Transactional      │
│              │         │  (Camunda)      │  REST   │ Service            │
│  Camunda     │         │  Native         │         │  (Base de Datos)   │
└──────────────┘         └─────────────────┘         └────────────────────┘
```

**Ejemplos en el proceso:**

### 1. Registrar Cliente
```xml
<camunda:inputParameter name="url">http://localhost:8083/api/clientes</camunda:inputParameter>
<camunda:inputParameter name="method">POST</camunda:inputParameter>
```

### 2. Registrar Pago
```xml
<camunda:inputParameter name="url">http://localhost:8083/api/facturacion/pago</camunda:inputParameter>
<camunda:inputParameter name="method">POST</camunda:inputParameter>
```

### 3. Activar Servicio
```xml
<camunda:inputParameter name="url">
  <![CDATA[http://localhost:8083/api/facturacion/activar?businessKey=${execution.getBusinessKey()}&cedulaCliente=${execution.getVariable("cedula")}]]>
</camunda:inputParameter>
<camunda:inputParameter name="method">POST</camunda:inputParameter>
```

## Resumen de Implementaciones en el Proceso

| Service Task | Tipo | Implementación | Propósito |
|--------------|------|----------------|-----------|
| **Guardar Solicitud** | Connector | HTTP POST /api/solicitudes | Guardar solicitud en BD |
| **Calcular Descuento** | Expression (método) | `calculadoraService.calcularDescuento()` | Cálculo de descuento |
| **Calcular Monto Final** | Expression (método) | `calculadoraService.calcularMontoFinal()` | Cálculo monto final |
| **Registrar Cliente** | Connector | HTTP POST /api/clientes | Guardar cliente en BD |
| **Registrar Pago** | Connector | HTTP POST /api/facturacion/pago | Guardar pago en BD |
| **Activar Servicio** | Connector | HTTP POST /api/facturacion/activar | Activar servicio en BD |
| **Limpiar Variables** | Delegate Expression | `limpiarVariablesDelegate` | Limpieza de datos |

## APIs del Transactional Service

### Endpoint: Registrar Cliente
```http
POST http://localhost:8083/api/clientes
Content-Type: application/json

{
  "cedula": "1234567890",
  "nombre": "Juan",
  "apellido": "Pérez",
  "direccion": "Calle 123",
  "ciudad": "Bogotá",
  "estrato": 2,
  "telefono": "3001234567",
  "email": "juan@example.com"
}
```

### Endpoint: Registrar Pago
```http
POST http://localhost:8083/api/facturacion/pago
Content-Type: application/json

{
  "businessKey": "process-123",
  "cedulaCliente": "1234567890",
  "montoRecibido": 500000,
  "metodoPago": "efectivo",
  "numeroTransaccion": "TX-123456",
  "fechaPago": "2025-11-23T10:00:00",
  "comprobanteUrl": "http://comprobante.com/123",
  "observacionesPago": "Pago completo"
}
```

### Endpoint: Activar Servicio
```http
POST http://localhost:8083/api/facturacion/activar?businessKey=process-123&cedulaCliente=1234567890
```

## Configuración

### application.properties (bpmn-service)
```properties
# URL del servicio transaccional
transactional.service.url=http://localhost:8083
```

### RestTemplate Bean
```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}
```

## Mejores Prácticas

1. **Java Class**: Para lógica compleja y autocontenida
2. **Delegate Expression**: Para beans Spring reutilizables
3. **Expression (método)**: Para lógica de negocio sin necesidad de delegates
4. **Expression (aritmética)**: Para cálculos simples
5. **Connector**: Para integraciones con sistemas externos

## Testing

Para probar las implementaciones:

1. Iniciar `transactional-service` en puerto 8083
2. Iniciar `bpmn-service` en puerto 8080
3. Ejecutar proceso desde `client-app` o `agent-app`
4. Verificar logs y base de datos H2

## Base de Datos

Verificar datos en H2:
- **Camunda DB**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./camunda-db/camunda-h2-database`
  
- **Transactional DB**: http://localhost:8083/h2-console  
  - JDBC URL: `jdbc:h2:file:./transactional-db/transactional-h2-database`
  - Tablas: `clientes`, `facturacion`, `solicitudes`
