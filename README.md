# 💰 TDS-25-26: ExpenseManager

![Java](https://img.shields.io/badge/Java-21-orange)
![Build](https://img.shields.io/badge/Build-Maven-blue)
![Architecture](https://img.shields.io/badge/Architecture-MVC-green)
![Status](https://img.shields.io/badge/Status-Completed-success)

Proyecto de aplicación de escritorio para la gestión integral de gastos personales y compartidos, desarrollado bajo los principios de **Clean Code**, **SOLID** y **Patrones de Diseño**.

Asignatura: **(1905) Tecnologías de Desarrollo de Software**.
Curso académico: 2025-2026.

---

## 👥 Equipo de Desarrollo

| Nombre | Email | Subgrupo |
| :--- | :--- | :--- |
| **Jorge Torralba Santa Cruz** | `jorge.torralbas@um.es` | 1.3 |
| **Juan Paredes Pardines** | `juan.paredesp@um.es` | 1.3 |
| **Guillermo Fulgencio Parra López** | `gf.parralopez@um.es` | 1.3 |

---

## 📝 Descripción del Sistema

**ExpenseManager** es una solución robusta para el control financiero. A diferencia de un gestor de gastos simple, este sistema implementa lógica compleja para **cuentas compartidas** (pisos de estudiantes, viajes, parejas) y un sistema de **alertas proactivas** basadas en estrategias temporales.

### Funcionalidades Clave
* **💰 Gestión de Gastos (CRUD):** Registro detallado con fecha, importe, categoría y pagador. Soporte para edición y borrado seguro.
* **🤝 Cuentas Compartidas:** Gestión de grupos con algoritmos de reparto configurables (**Equitativo** vs **Porcentual**) para el cálculo automático de deudas entre usuarios.
* **🔔 Sistema de Alertas:** Notificaciones en tiempo real al superar límites de gasto (Semanal o Mensual) definidas por el usuario.
* **📊 Dashboard Estadístico:** Visualización de datos mediante gráficos interactivos y filtrado avanzado (por fechas y categorías múltiples).
* **📥 Importación Masiva:** Carga de datos desde ficheros CSV externos mediante adaptadores robustos.
* **💾 Persistencia:** Almacenamiento local automático en formato **JSON** utilizando la librería Jackson.

---

## 🏗 Arquitectura y Diseño Técnico

El proyecto ha sido diseñado siguiendo estrictamente el patrón **MVC (Modelo-Vista-Controlador)**.

### 1. Catálogo de Patrones de Diseño (GoF)
Hemos implementado los siguientes patrones para garantizar la escalabilidad y mantenibilidad, cumpliendo con los requisitos más exigentes de la asignatura:

| Patrón | Implementación en el Proyecto |
| :--- | :--- |
| **Singleton** | Gestión única de la sesión (`ControladorSesion`) y factorías (`FactoriaEstrategia`, `FactoriaImportadores`). |
| **Strategy** | Algoritmos intercambiables para el reparto de gastos (`RepartoEquitativo`, `RepartoPorcentual`) y lógica temporal de alertas (`EstrategiaAlertaMensual`, `EstrategiaAlertaSemanal`). |
| **Factory Method** | Creación dinámica de estrategias e importadores según el contexto, desacoplando la instanciación de la lógica de negocio. |
| **Observer** | Comunicación reactiva: El `ControladorAppGastos` notifica automáticamente a las Vistas (`IObservador`) cuando hay cambios en el modelo (nuevos gastos, alertas disparadas). |
| **Adapter** | Adaptación de ficheros CSV externos al dominio de la aplicación (`ImportadorCSV` -> `ImportadorGastos`). |
| **Repository (DAO)** | Abstracción de la capa de datos (`RepositorioGastosJSON`, `RepositorioCuentasJSON`), permitiendo cambiar el almacenamiento sin afectar al dominio. |

### 2. Calidad de Código (Clean Code)
* **Diseño por Contrato:** Validaciones defensivas en constructores y setters para asegurar la integridad de los datos (evitar importes negativos, nulos, etc.).
* **Java Moderno (Streams & Lambdas):** Uso extensivo de la API Stream para cálculos financieros (sumas, promedios) y filtrado de colecciones, evitando bucles imperativos complejos y mejorando la legibilidad.
* **Inmutabilidad:** Protección de las estructuras de datos internas mediante copias defensivas y listas inmodificables (`Collections.unmodifiableList`).

---

## 📊 Diagramas de Arquitectura

### Diagrama de Clases
Refleja la estructura estática del sistema y las relaciones entre capas.

![Diagrama de Clases UML](./docs/imagenes/diagrama_clases.png)

### Diagrama de Secuencia (Caso de Uso: Alertas)
Muestra la interacción dinámica al registrar un gasto y disparar una notificación.

![Diagrama de Secuencia](./docs/imagenes/diagrama_secuencia.png)

---

## 🚀 Instalación y Ejecución

### Requisitos Técnicos
* **Java JDK 21** (Obligatorio).
* **Maven 3.8+**.

### Opción A: Interfaz Gráfica (JavaFX)
Es el modo principal de la aplicación.

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/GuillermoPL/TDS-25-26.git](https://github.com/GuillermoPL/TDS-25-26.git)
    cd TDS-25-26
    ```
2.  **Compilar y Ejecutar:**
    ```bash
    mvn clean javafx:run
    ```

### Opción B: Modo Consola (CLI)
Incluimos una interfaz de línea de comandos para mantenimiento y pruebas rápidas del modelo sin cargar la GUI.

1.  **Ejecutar comando Maven:**
    ```bash
    mvn exec:java -Dexec.mainClass="umu.tds.MainCLI"
    ```
2.  **Menú Interactivo:**
    El sistema iniciará sesión automáticamente con un usuario de prueba y permitirá listar, crear y borrar gastos personales.

---

## 📂 Estructura del Proyecto

```text
src/main/java/umu/tds
├── adapters      # Implementación de persistencia (JSON) e importación (CSV)
├── controlador   # Lógica de coordinación, fachada y gestión de eventos
├── modelo        # Entidades, Estrategias, Factorías y Reglas de Negocio
└── vista         # Controladores de JavaFX (FXML) e interfaz IObservador
```

## 📚 Documentación Adicional

Toda la documentación técnica y de gestión del proyecto se encuentra disponible en el directorio [`/docs`](./docs):

### 🏗️ [Arquitectura y Decisiones de Diseño](./docs/ARQUITECTURA_Y_DECISIONES_DE_DISEÑO.pdf)
Documento que justifica las decisiones de ingeniería del software:
* **Arquitectura MVC:** Separación de responsabilidades entre Modelo, Vista (JavaFX/FXML) y Controlador (`ControladorAppGastos`).
* **Decisiones Clave:** Centralización de instancias mediante `Configuracion` (Singleton), desacoplamiento de la persistencia con Adapter (JSON/Jackson) y modelo de delegación de eventos (Observer).
* **Integridad:** Estrategias para evitar duplicados en la importación.

### 🧩 [Patrones de Diseño](./docs/PATRONES_DE_DISEÑO.pdf)
Análisis exhaustivo de los patrones GoF y principios GRASP aplicados:
* **Creacionales:** Uso de **Singleton** (Sesión, Factorías) y **Factory Method** para la creación dinámica de estrategias e importadores.
* **Estructurales:** Patrón **Adapter** para la persistencia e importación de ficheros.
* **Comportamiento:** **Observer** para la sincronización de vistas y **Strategy** para la lógica de reparto y alertas.
* **Principios GRASP:** Aplicación de Experto, Creador y Controlador.

### 📋 [Historias de Usuario (TDS)](./docs/TDS_HISTORIAS_DE_USUARIO.pdf)
Especificación funcional detallada dividida en 5 Épicas:
1.  **Gestión de Gastos:** CRUD completo y validaciones.
2.  **Visualización:** Listados, gráficos y filtrado avanzado.
3.  **Sistema de Alertas:** Configuración de límites y notificaciones.
4.  **Cuentas Compartidas:** Gestión de grupos y repartos (Equitativo/Porcentual).
5.  **Importación:** Carga de datos externos.

### 📕 [Manual de Usuario](./docs/MANUAL_USUARIO.pdf)
Guía visual para el despliegue y uso de la aplicación:
* Instrucciones de instalación y ejecución.
* Recorrido por la interfaz gráfica.
* Guía de uso del modo consola (CLI).


