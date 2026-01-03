# TDS-25-26 - ExpenseManager

Proyecto desarrollado para la asignatura **(1905) Tecnologías de Desarrollo de Software**.  
Curso académico: 2025-2026.

---

## 👥 Integrantes del Grupo

| Nombre | Email | Subgrupo |
| :--- | :--- | :--- |
| Jorge Torralba Santa Cruz | `jorge.torralbas@um.es` | 1.3 |
| Juan Paredes Pardines | `juan.paredesp@um.es` | 1.3 |
| Guillermo Fulgencio Parra López | `gf.parralopez@um.es` | 1.3 |

---

## 📝 Descripción del Proyecto

**ExpenseManager** es una aplicación de escritorio robusta orientada a la gestión de finanzas personales y grupales. El sistema permite un control detallado del flujo de caja mediante las siguientes funcionalidades principales:

* **Gestión Integral de Gastos:** CRUD completo de gastos con categorización dinámica.
* **Visualización Estadística:** Generación de gráficos de tarta (PieChart) y filtrado avanzado por rango de fechas y categorías.
* **Sistema de Alertas Inteligentes:** Implementación de presupuestos límite con periodos semanales o mensuales mediante el **Patrón Estrategia**. El sistema notifica al usuario en tiempo real mediante el **Patrón Observador**.
* **Cuentas Compartidas:** Soporte para gastos comunes con lógica de reparto configurable (Equitativo o Porcentual) para la gestión de deudas grupales.
* **Persistencia de Datos:** Almacenamiento local automático en formato **JSON** utilizando la librería Jackson.

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
* **Java JDK 21** o superior.
* **Maven 3.8+** (recomendado).

### Instrucciones de Ejecución
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/GuillermoPL/TDS-25-26.git](https://github.com/GuillermoPL/TDS-25-26.git)
    ```
2.  **Ejecutar mediante Maven:**
    Desde la raíz del proyecto, ejecuta el siguiente comando para compilar y lanzar la interfaz gráfica:
    ```bash
    mvn clean javafx:run
    ```
3.  **Ejecutar desde IDE (Eclipse/IntelliJ):**
    * Importar el proyecto como **Maven Project**.
    * Asegurarse de que el `module-info.java` está correctamente configurado.
    * Lanzar la clase `umu.tds.App`.

---

## 📚 Documentación

Toda la documentación técnica adicional se encuentra organizada en la carpeta [`/docs`](./docs) del repositorio:

* [**Memoria Técnica**](./docs/memoria.md): Explicación de la arquitectura, patrones de diseño aplicados y cumplimiento de requisitos.
* [**Manual de Usuario**](./docs/manual.md): Guía visual para el uso de la aplicación.
* **Diagramas:** Los diagramas de clase y de secuencia se encuentran en [`/docs/imágenes`](./docs/imágenes).

---
