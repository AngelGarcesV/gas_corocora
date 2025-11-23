# Sistema de Instalación de Gas - Gas Corocora

Sistema de gestión de solicitudes de instalación de gas natural con Camunda BPM embebido y notificaciones por correo electrónico.

## Requisitos Previos

- **Java 21** (JDK 21 o superior)
- **Maven 3.6+**
- **Conexión a Internet** (para envío de correos vía Gmail)

## Instalación y Ejecución

### 1. Configurar Credenciales de Gmail

Editar el archivo `src/main/resources/application.properties` con tu configuración de Gmail:

```properties
# Configuración de correo electrónico
spring.mail.username=TU_CORREO@gmail.com
spring.mail.password=TU_CONTRASEÑA_DE_APLICACION
```

> ** Importante**: Debes generar una "Contraseña de aplicación" en tu cuenta de Google:
> 1. Ve a https://myaccount.google.com/security
> 2. Activa la verificación en 2 pasos
> 3. En "Contraseñas de aplicaciones", genera una nueva para "Correo"
> 4. Usa esa contraseña en `spring.mail.password`

### 2. Compilar el Proyecto

```bash
cd d:\Universidad\springboot-bpmn
mvn clean install
```

### 3. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:
- **Aplicación**: http://localhost:8080
- **Camunda Cockpit**: http://localhost:8080/camunda/app/cockpit/default/
- **Camunda Tasklist**: http://localhost:8080/camunda/app/tasklist/default/
- **Camunda Admin**: http://localhost:8080/camunda/app/admin/default/

**Credenciales por defecto:**
- Usuario: `demo`
- Contraseña: `demo`

## Guía de Ejecución del Proceso BPMN

### Opción 1: Ejecución vía REST API (Recomendado)

#### Paso 1: Crear una Nueva Solicitud

```bash
curl -X POST "http://localhost:8080/api/solicitudes" \
  -d "nombre=Juan Pérez" \
  -d "email=juanperez@example.com" \
  -d "telefono=3001234567" \
  -d "direccion=Calle 123 #45-67" \
  -d "estrato=3" \
  -d "tipoInstalacion=Residencial"
```

**Respuesta esperada:**
```json
{
  "success": true,
  "solicitudId": "SOL-2024-001",
  "procesoInstanceId": "f8e123a4-5678-90ab-cdef-1234567890ab",
  "estado": "RADICADA",
  "mensaje": "Solicitud creada exitosamente. Proceso BPMN iniciado.",
  "data": {
    "solicitudId": "SOL-2024-001",
    "nombre": "Juan Pérez",
    "email": "juanperez@example.com",
    ...
  }
}
```

** Email enviado automáticamente**: El usuario recibirá un correo de confirmación con el ID de su solicitud.

#### Paso 2: Consultar Estado de la Solicitud

```bash
curl -X GET "http://localhost:8080/api/solicitudes/SOL-2024-001"
```

#### Paso 3: Ver Historial de la Solicitud

```bash
curl -X GET "http://localhost:8080/api/solicitudes/SOL-2024-001/historial"
```

#### Paso 4: Ver Estadísticas Globales

```bash
curl -X GET "http://localhost:8080/api/estadisticas"
```

### Opción 2: Ejecución Manual vía Camunda Tasklist

#### Paso 1: Iniciar el Proceso

1. Ir a **Camunda Tasklist**: http://localhost:8080/camunda/app/tasklist/default/
2. Hacer clic en **"Start process"** (icono de +)
3. Seleccionar: **"Instalación de Gas Natural - Gas Corocora"**
4. Llenar el formulario "Radicar Solicitud" con:
   - **Nombre**: Juan
   - **Apellido**: Pérez
   - **Email**: juanperez@example.com *(campo obligatorio para notificaciones)*
   - **Teléfono**: 3001234567
   - **Dirección**: Calle 123 #45-67
   - **Ciudad**: Bogotá
   - **Estrato**: 3
5. Hacer clic en **"Start"**

** Email enviado**: Confirmación de solicitud radicada.

#### Paso 2: Verificar Viabilidad Técnica

1. En **Tasklist**, verás una nueva tarea: **"Verificar viabilidad técnica"**
2. Hacer clic en la tarea
3. Llenar el formulario:
   - **¿Es viable técnicamente?**: Seleccionar "Sí" o "No"
   - **Observaciones**: Agregar comentarios (opcional)
4. Hacer clic en **"Complete"**

**Si seleccionas "No":**
- ** Email enviado**: Notificación de solicitud inviable
- El proceso termina

**Si seleccionas "Sí":**
- Continúa a verificación de documentación

#### Paso 3: Verificar Documentación

1. Nueva tarea: **"Verificar documentación del cliente"**
2. Llenar el formulario:
   - **¿Documentación completa?**: Seleccionar "Sí" o "No"
   - **Documentos faltantes**: Si es "No", especificar
3. Hacer clic en **"Complete"**

**Si seleccionas "No":**
- ** Email enviado**: Solicitud de documentos faltantes
- El proceso puede esperar o cancelarse según la configuración

**Si seleccionas "Sí":**
- Continúa a elaboración de cotización

#### Paso 4: Elaborar Cotización

1. Nueva tarea: **"Elaborar cotización"**
2. Llenar el formulario:
   - **Monto de conexión**: Ej: 450000
   - **Descuento aplicado**: Ej: 10
3. Hacer clic en **"Complete"**

** Email enviado**: Cotización con el monto y descuento aplicado.

#### Paso 5: Cliente Decide sobre Cotización

1. Nueva tarea: **"Cliente: Aceptar o rechazar cotización"**
2. Llenar el formulario:
   - **¿Acepta la cotización?**: Seleccionar "Sí" o "No"
3. Hacer clic en **"Complete"**

**Si seleccionas "No":**
- ** Email enviado**: Cancelación de solicitud
- El proceso termina

**Si seleccionas "Sí":**
- Continúa a programación de instalación

#### Paso 6: Programar Instalación

1. Nueva tarea: **"Programar instalación"**
2. Llenar el formulario:
   - **Fecha de instalación**: Ej: 2024-06-15
   - **Técnico asignado**: Ej: Carlos González
3. Hacer clic en **"Complete"**

#### Paso 7: Realizar Instalación

1. Nueva tarea: **"Realizar instalación en sitio"**
2. Llenar el formulario:
   - **¿Instalación exitosa?**: Seleccionar "Sí" o "No"
   - **Observaciones**: Detalles de la instalación
3. Hacer clic en **"Complete"**

**Si seleccionas "No":**
- ** Email enviado**: Notificación de problemas en instalación
- El proceso puede regresar a reprogramación

**Si seleccionas "Sí":**
- Continúa a instalación de medidor

#### Paso 8: Instalar Medidor

1. Nueva tarea: **"Instalar medidor de gas"**
2. Llenar el formulario:
   - **Número de medidor**: Ej: MED-2024-12345
3. Hacer clic en **"Complete"**

#### Paso 9: Verificar Instalación

1. Nueva tarea: **"Verificar instalación y realizar pruebas"**
2. Llenar el formulario:
   - **¿Verificación exitosa?**: Seleccionar "Sí"
   - **Resultado de pruebas**: Ej: Todas las pruebas pasaron correctamente
3. Hacer clic en **"Complete"**

#### Paso 10: Activar Servicio

El sistema automáticamente activa el servicio.

** Email enviado**: Confirmación de activación del servicio de gas.

#### Paso 11: Facturación

El sistema inicia automáticamente el proceso de facturación.

** Email enviado**: Notificación de inicio de facturación.

** Proceso completado exitosamente.**

##  Monitoreo del Proceso

### Ver en Camunda Cockpit

1. Ir a: http://localhost:8080/camunda/app/cockpit/default/
2. Hacer clic en **"Processes"**
3. Seleccionar: **"Instalación de Gas Natural - Gas Corocora"**
4. Ver todas las instancias activas y completadas
5. Hacer clic en una instancia para ver el diagrama con el estado actual

### Ver Logs de la Aplicación

Los logs muestran cada paso del proceso:
```
INFO  - Creando solicitud para: Juan Pérez - juanperez@example.com
INFO  - Solicitud creada: SOL-2024-001
INFO  - Proceso iniciado: f8e123a4-5678-90ab-cdef-1234567890ab
INFO  - Email enviado a juanperez@example.com
```

## Base de Datos

La aplicación usa **H2 in-memory** para desarrollo. Los datos se almacenan en:

- **Tablas de Camunda**: Gestión de procesos, tareas, variables
- **Tabla `solicitud_instalacion`**: Datos de solicitudes de clientes
- **Tabla `historial_proceso`**: Registro de eventos del proceso

### Acceder a la Consola H2

1. Ir a: http://localhost:8080/h2-console
2. Configuración:
   - **JDBC URL**: `jdbc:h2:mem:camunda`
   - **User**: `sa`
   - **Password**: *(dejar en blanco)*
3. Hacer clic en **"Connect"**

### Consultas Útiles

```sql
-- Ver todas las solicitudes
SELECT * FROM solicitud_instalacion ORDER BY fecha_creacion DESC;

-- Ver historial de una solicitud
SELECT * FROM historial_proceso WHERE solicitud_id = 'SOL-2024-001' ORDER BY fecha_evento;

-- Estadísticas por estado
SELECT estado, COUNT(*) as total FROM solicitud_instalacion GROUP BY estado;
```

## Notificaciones por Email

El sistema envía correos automáticamente en los siguientes eventos:

| Evento | Destinatario | Contenido |
|--------|--------------|-----------|
| **Solicitud Radicada** | Cliente | Confirmación con ID de solicitud |
| **Solicitud Inviable** | Cliente | Razones de rechazo técnico |
| **Envío de Cotización** | Cliente | Monto, descuento, y detalles |
| **Cancelación** | Cliente | Confirmación de cancelación |
| **Problemas en Instalación** | Cliente | Descripción del problema |
| **Servicio Activado** | Cliente | Confirmación de activación |
| **Facturación Iniciada** | Cliente | Información de cobro |

## Estructura del Proyecto

```
springboot-bpmn/
├── src/main/java/com/example/demo/
│   ├── CamundaDemoApplication.java          # Clase principal
│   ├── config/
│   │   ├── CamundaConfig.java               # Configuración Camunda
│   │   └── ResourceDeploymentConfig.java    # Despliegue de recursos
│   ├── controller/
│   │   └── ProcessController.java           # REST API
│   ├── delegate/
│   │   ├── ActivarServicioDelegate.java
│   │   ├── CancelarSolicitudDelegate.java
│   │   ├── EnviarCotizacionDelegate.java
│   │   ├── IniciarFacturacionDelegate.java
│   │   ├── LimpiarVariablesVerificacionDelegate.java
│   │   ├── NotificarProblemasInstalacionDelegate.java
│   │   └── NotificarSolicitudInviableDelegate.java
│   ├── model/
│   │   ├── HistorialProceso.java            # Entidad JPA
│   │   └── SolicitudInstalacion.java        # Entidad JPA
│   ├── repository/
│   │   ├── HistorialProcesoRepository.java
│   │   └── SolicitudInstalacionRepository.java
│   └── service/
│       ├── EmailService.java                # Servicio de emails
│       └── SolicitudInstalacionService.java # Lógica de negocio
├── src/main/resources/
│   ├── application.properties               # Configuración
│   └── instalación gas/
│       ├── instalacion_gas_subproceso.bpmn  # Proceso BPMN
│       ├── tablas-decision/
│       │   └── tabla-descuento.dmn          # Tabla DMN
│       └── forms/                           # 10 formularios Camunda
└── pom.xml                                  # Dependencias Maven
```

## Pruebas

### Probar Endpoints REST

```bash
# Health check
curl http://localhost:8080/api/health

# Crear solicitud
curl -X POST "http://localhost:8080/api/solicitudes" \
  -d "nombre=María García" \
  -d "email=maria@example.com" \
  -d "telefono=3109876543" \
  -d "direccion=Carrera 10 #20-30" \
  -d "estrato=2"

# Listar todas las solicitudes
curl http://localhost:8080/api/solicitudes

# Ver estadísticas
curl http://localhost:8080/api/estadisticas
```

## Solución de Problemas

### El proceso no aparece en Camunda

**Solución**: Verificar logs. El sistema despliega automáticamente todos los recursos BPMN, DMN y FORM al iniciar.

```
INFO  - Recursos desplegados exitosamente: 12 archivos
INFO  - Proceso desplegado: Process_19yx4pt
```

### No llegan los correos electrónicos

**Verificar**:
1. Credenciales de Gmail correctas en `application.properties`
2. Contraseña de aplicación (no la contraseña normal de Gmail)
3. Conexión a Internet activa
4. Variable `cliente_email` definida en el proceso

### Error: "java: release version 5 not supported"

**Solución**: Verificar que tienes Java 21 instalado:
```bash
java -version
```

Debe mostrar: `openjdk version "21.x.x"`

## Tecnologías Utilizadas

- **Spring Boot 2.7.18**
- **Camunda BPM 7.18.0**
- **Java 21**
- **H2 Database**
- **Spring Data JPA**
- **Spring Mail**
- **Thymeleaf**
- **Maven**

## Autor

Sistema desarrollado para Gas Corocora - Gestión de Instalaciones de Gas Natural

## Licencia

Este proyecto es de uso académico para la Universidad.
