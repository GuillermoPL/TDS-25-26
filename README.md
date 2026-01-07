# 💰 TDS-25-26: ExpenseManager

![Java](https://img.shields.io/badge/Java-21-orange)
![Build](https://img.shields.io/badge/Build-Maven-blue)
![Architecture](https://img.shields.io/badge/Architecture-MVC-green)
![Status](https://img.shields.io/badge/Status-Completed-success)

Proyecto de aplicación de escritorio para la gestión integral de gastos personales y compartidos, desarrollado bajo los principios de **Clean Code**, **SOLID** y **Patrones de Diseño**.

Asignatura: **(1905) Tecnologías de Desarrollo de Software**.
Curso académico: 2025-2026.

---

## 👥 Equipo de Desarrollo (Subgrupo 1.3)

| Nombre | Email | Rol Principal |
| :--- | :--- | :--- |
| **Jorge Torralba Santa Cruz** | `jorge.torralbas@um.es` | Backend & Persistencia |
| **Juan Paredes Pardines** | `juan.paredesp@um.es` | Frontend (JavaFX) |
| **Guillermo Fulgencio Parra López** | `gf.parralopez@um.es` | Arquitectura & Controladores |

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
| **Singleton** | Gestión única de la sesión (`ControladorSesion`) y factorías (`FactoriaEstrategia`, `FactoriaImportadores`). Implementación *Eager* para garantizar Thread-Safety. |
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

## 📊 Diagrama de Clases

La siguiente imagen refleja la arquitectura actual del sistema (MVC + Patrones):

![Diagrama de Clases UML](./docs/imagenes/diagrama_clases.png)

> *El diagrama completo muestra las relaciones entre Controladores, Modelos y Vistas, destacando la implementación de los patrones Strategy y Observer.*

---

## 🚀 Instalación y Ejecución

### Requisitos Técnicos
* **Java JDK 21** (Obligatorio).
* **Maven 3.8+**.

### Pasos para ejecutar
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/GuillermoPL/TDS-25-26.git](https://github.com/GuillermoPL/TDS-25-26.git)
    cd TDS-25-26
    ```
2.  **Compilar y Ejecutar (Maven):**
    ```bash
    mvn clean javafx:run
    ```
3.  **Desde Eclipse / IntelliJ:**
    * Importar el directorio como "Maven Project".
    * Ejecutar la clase principal: `umu.tds.App`.

---

## 📂 Estructura del Proyecto

```text
src/main/java/umu/tds
├── adapters      # Implementación de persistencia (JSON) e importación (CSV)
├── controlador   # Lógica de coordinación, fachada y gestión de eventos
├── modelo        # Entidades, Estrategias, Factorías y Reglas de Negocio
└── vista         # Controladores de JavaFX (FXML) e interfaz IObservador
```
