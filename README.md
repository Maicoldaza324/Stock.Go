# Stock.Go — Sistema de Gestión de Inventarios

Aplicación móvil para la gestión y el control de inventarios, desarrollada en Kotlin para dispositivos Android. Permite administrar productos, categorías, existencias y movimientos de inventario mediante una interfaz intuitiva y una base de datos en la nube.

## Descripción del proyecto

Stock.Go es una aplicación diseñada para facilitar la administración de inventarios de pequeños y medianos negocios.

La aplicación centraliza la información de los productos y permite controlar las entradas y salidas de mercancía, consultar el historial de movimientos y supervisar los niveles de existencias.

La información se almacena en Firebase, permitiendo que los usuarios autorizados consulten y gestionen los datos desde la aplicación.

## Objetivo general

Desarrollar una aplicación móvil que facilite el registro, administración y control de inventarios mediante herramientas de gestión de productos, categorías y movimientos, garantizando la disponibilidad y actualización de la información.

## Funcionalidades principales

### Autenticación y usuarios
- Inicio y cierre de sesión.
- Autenticación mediante correo electrónico y contraseña.
- Inicio de sesión con Google.
- Gestión de usuarios.
- Asignación de roles y control de acceso.
- Activación y desactivación de usuarios.

### Gestión de productos
- Registro de productos.
- Consulta y búsqueda de productos.
- Edición y eliminación de productos.
- Administración de existencias.
- Configuración de niveles mínimos de stock.
- Consulta del detalle de cada producto.

### Gestión de categorías
- Creación de categorías.
- Consulta de categorías.
- Edición y eliminación de categorías.
- Actualización de los productos asociados cuando se modifica una categoría.

### Movimientos de inventario
- Registro de entradas de productos.
- Registro de salidas de productos.
- Actualización automática de las existencias.
- Validación de cantidades disponibles.
- Registro del usuario responsable de cada movimiento.
- Consulta y filtrado del historial de movimientos.

### Dashboard
- Visualización general del inventario.
- Consulta de productos disponibles.
- Identificación de productos con stock bajo.
- Identificación de productos agotados.

## Roles del sistema

**Administrador**
- Gestionar usuarios y sus roles.
- Crear, editar y eliminar productos.
- Administrar categorías.
- Consultar el inventario y los movimientos.

**Empleado**
- Consultar productos y categorías.
- Registrar entradas y salidas de inventario.
- Consultar el historial de movimientos.
- Consultar información del inventario sin acceder a las funciones administrativas.

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje de programación |
| Android Studio | Entorno de desarrollo |
| Jetpack Compose | Construcción de interfaces |
| Material 3 | Componentes visuales |
| Firebase Authentication | Autenticación de usuarios |
| Cloud Firestore | Almacenamiento de información |
| Git y GitHub | Control de versiones |

## Arquitectura y organización

El proyecto está organizado en diferentes paquetes para separar las responsabilidades de la aplicación:

- `Auth`: autenticación y administración de usuarios mediante Firebase.
- `Components`: componentes reutilizables de interfaz.
- `Data`: repositorios encargados de la comunicación con Firestore.
- `Model`: modelos de datos.
- `Screens`: pantallas y funcionalidades de la aplicación.
- `MainActivity.kt`: punto de entrada y navegación principal.

## Base de datos

Stock.Go utiliza Cloud Firestore como base de datos NoSQL en la nube.

Las principales colecciones utilizadas son:

- `usuarios`: información y roles de los usuarios.
- `productos`: información de los productos y sus existencias.
- `categorias`: categorías de los productos.
- `movimientos`: registros de entradas y salidas de inventario.

El acceso a la información está controlado mediante reglas de seguridad de Firebase.

## Instalación y ejecución

### Requisitos
- Android Studio.
- JDK compatible con la configuración del proyecto.
- Gradle Wrapper incluido en el repositorio.
- Una cuenta de Firebase con un proyecto configurado.
- Un dispositivo Android o emulador.

### Pasos

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/Maicoldaza324/Stock.Go.git
   ```

2. Abrir el proyecto desde Android Studio.

3. Configurar Firebase y agregar el archivo `google-services.json` en la carpeta `app/`.

4. Sincronizar el proyecto con Gradle.

5. Ejecutar la aplicación desde Android Studio en un dispositivo o emulador.

## Estado del proyecto

**Versión 0.2 — Funcionalidades principales implementadas.**

La aplicación cuenta con autenticación, gestión de usuarios, productos, categorías y movimientos de inventario, con persistencia de información en Firebase.

Se han realizado pruebas funcionales de los módulos principales y de los permisos correspondientes a los roles de administrador y empleado.

## Equipo de trabajo

- Maicol Andres Daza Acuña
- Juan David Hoyos Avila

## Repositorio

[Stock.Go en GitHub](https://github.com/Maicoldaza324/Stock.Go)

