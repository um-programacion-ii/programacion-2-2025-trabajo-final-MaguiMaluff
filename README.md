# Sistema de Registro de Asistencia a Eventos

Proyecto final - Programación 2 (2025)

Alumno: María Magdalena Maluff Stabio

Legajo: 62234

Resumen
-------
Este repositorio contiene la implementación del sistema para registrar asistencia a eventos únicos (charlas, cursos, obras de teatro, etc.). El objetivo es gestionar eventos, la selección y bloqueo de asientos, y la venta de entradas mediante una arquitectura distribuida compuesta por varios servicios.

Arquitectura
------------
El sistema se divide en los siguientes componentes principales, cada uno ubicado en su carpeta raíz dentro de este repositorio:

- `backend`  
  Backend (implementado en Java / Spring Boot - JHipster). Maneja la lógica de negocio, persistencia local de eventos y ventas, autenticación de usuarios y sesiones.

- `proxy`  
  Servicio proxy que actúa como intermediario con los servicios de la cátedra (accede a Redis y Kafka de la cátedra). Se encarga de leer notificaciones y exponer/normalizar la información para el backend.

- `kmp`  
  Cliente móvil implementado en Kotlin Multiplatform (KMP). Provee la interfaz de usuario para listar eventos, seleccionar asientos, cargar datos de personas y realizar la compra.

Archivos importantes
--------------------
- `Enunciado Trabajo Final 2025 - v1.2.pdf` — especificación y payloads del sistema (ubicado en el repo).
- Carpetas raíz: `backend/`, `proxy/`, `kmp/` — cada una contiene el código correspondiente al componente.