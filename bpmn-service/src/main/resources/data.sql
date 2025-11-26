-- bpmn-service/src/main/resources/data.sql
-- Script de inicialización de datos para la base de datos H2
-- Se ejecuta automáticamente al iniciar la aplicación

INSERT INTO proveedor (nombre, precio_por_kg, tiempo_entrega_dias, vigencia_meses, email, telefono_contacto, estado, descripcion, ciudad, fecha_creacion, fecha_actualizacion)
VALUES 
('GasTotal', 1200.00, 3, 12, 'contacto@gastotal.com', '+57 1 234 5678', 'ACTIVO', 'Proveedor nacional de gas con amplia cobertura', 'Bogotá', NOW(), NOW()),
('GasCity', 1150.00, 5, 6, 'ventas@gascity.com', '+57 1 345 6789', 'ACTIVO', 'Distribuidor especializado en compras a granel', 'Medellín', NOW(), NOW()),
('Distribuidora X', 1180.00, 2, 18, 'pedidos@distribuidorax.com', '+57 1 456 7890', 'ACTIVO', 'Líder en entregas rápidas de gas', 'Cali', NOW(), NOW()),
('GasExpress', 1220.00, 1, 9, 'express@gasexpress.com', '+57 1 567 8901', 'ACTIVO', 'Entregas express en 24 horas', 'Barranquilla', NOW(), NOW()),
('AltaPresión', 1100.00, 7, 24, 'contacto@altapresion.com', '+57 1 678 9012', 'ACTIVO', 'Precios competitivos con contrato extendido', 'Bogotá', NOW(), NOW());
