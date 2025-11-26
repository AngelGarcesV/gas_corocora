# Sistema de Instalación de Gas - Gas Corocora

Sistema de gestión de solicitudes de instalación de gas natural con Camunda BPM embebido y notificaciones por correo electrónico.

## 📋 Requisitos Previos

- **Java 21** (JDK 21 o superior)
- **Maven 3.6+**
- **Conexión a Internet** (para envío de correos vía Gmail)

## 🚀 Instalación y Ejecución

### 1. Configurar Credenciales de Gmail

Editar el archivo `src/main/resources/application.properties` con tu configuración de Gmail:

```properties
# Configuración de correo electrónico
spring.mail.username=TU_CORREO@gmail.com
spring.mail.password=TU_CONTRASEÑA_DE_APLICACION
```

> **⚠️ Importante**: Debes generar una "Contraseña de aplicación" en tu cuenta de Google:
> 1. Ve a https://myaccount.google.com/security
> 2. Activa la verificación en 2 pasos
> 3. En "Contraseñas de aplicaciones", genera una nueva para "Correo"
> 4. Usa esa contraseña en `spring.mail.password`

### 2. Compilar el Proyecto

```bash
cd c:\ubicacion\springboot-bpmn
mvn clean install
```

### 3. Ejecutar la Aplicación
-El siguiente paso se debe repetir para cada aplicativo:
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


## 🗄️ Base de Datos

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

## ❓ Solución de Problemas

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

## 📚 Tecnologías Utilizadas

- **Spring Boot 2.7.18**
- **Camunda BPM 7.18.0**
- **Java 21**
- **H2 Database**
- **Spring Data JPA**
- **Spring Mail**
- **Thymeleaf**
- **Maven**

## 👨‍💻 Autor

Sistema desarrollado para Gas Corocora

## 📄 Licencia

Este proyecto es de uso académico para la Universidad.
