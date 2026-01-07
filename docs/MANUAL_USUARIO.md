# 📕 Manual de Usuario - ExpenseManager

Bienvenido al manual de usuario de **ExpenseManager**. Esta guía le ayudará a comprender todas las funcionalidades de la aplicación, desde la gestión básica de gastos hasta el uso de cuentas compartidas y herramientas de análisis.

---

## 1. Ventana Principal: Gestión de Gastos

Al iniciar la aplicación, accederá a la vista principal. Aquí se centraliza la gestión de sus finanzas personales.

### 1.1. Tabla de Gastos
La zona central muestra el listado completo de sus gastos registrados. Puede ver rápidamente la fecha, categoría, concepto e importe de cada movimiento.

![Vista Principal](./imagenes/ventana_gastos.png)

### 1.2. Operaciones Básicas (CRUD)
En la parte derecha encontrará el panel de control para gestionar los registros:

* **Registrar Gasto:** Pulse el botón "Añadir". Se abrirá un diálogo donde debe indicar el importe, seleccionar una fecha y elegir una categoría.
    ![Nuevo Gasto](./imagenes/ventana_nuevo_gasto.png)

* **Editar Gasto:** Seleccione un gasto de la tabla y pulse "Editar" (o haga doble clic sobre la fila). Podrá modificar cualquier dato erróneo.
    ![Editar Gasto](./imagenes/ventana_editar_gasto.png)

* **Borrar Gasto:** Seleccione el gasto que desea eliminar y pulse "Eliminar". El sistema solicitará confirmación por seguridad.
    ![Borrar Gasto](./imagenes/ventana_borrar_gasto.png)

### 1.3. Filtrado Avanzado
Puede filtrar la tabla para analizar periodos o categorías específicas:
* **Por Fechas:** Seleccione un rango (ej: Abril a Agosto) para ver solo los gastos de ese periodo.
    ![Filtro Fechas](./imagenes/filtro_abril_agosto.png)
* **Combinado:** Puede sumar criterios, por ejemplo, ver solo los gastos de "Alimentación" ocurridos entre Abril y Agosto.
    ![Filtro Combinado](./imagenes/filtro_abril_agosto_alimentacion.png)

### 1.4. Importación de Datos
El botón **"Importar"** le permite cargar masivamente gastos desde un fichero externo (CSV). El sistema detectará automáticamente si un gasto ya existe para evitar duplicados.

---

## 2. Gestión de Categorías

Para organizar mejor sus gastos, puede crear categorías personalizadas desde el menú "Categorías".

![Gestión de Categorías](./imagenes/ventana_categorias.png)

> **Nota importante:** El nombre de la categoría debe ser único. Si intenta crear una categoría que ya existe, el sistema mostrará un error de validación.

![Error Categoría Inválida](./imagenes/categoria_invalida.png)

---

## 3. Sistema de Alertas y Notificaciones

ExpenseManager le ayuda a no gastar más de la cuenta mediante un sistema de alertas proactivas.

### 3.1. Configuración de Alertas
Desde la sección de alertas, puede establecer límites de gasto:
* **Temporales:** Defina un límite máximo para gastar en una semana o en un mes.
* **Por Categoría:** (Opcional) Limite el gasto en categorías específicas como "Ocio".

![Configuración Alertas](./imagenes/ventana_alertas.png)

### 3.2. Aviso de Límite Superado
Si al registrar un gasto se supera alguno de los límites configurados, saltará inmediatamente una ventana emergente avisándole del exceso.

![Alerta Disparada](./imagenes/salto_alerto.png)

### 3.3. Historial
Puede consultar todas las alertas que han saltado en el pasado desde la ventana "Historial de Notificaciones".

![Historial](./imagenes/historial_notificaciones.png)

---

## 4. Cuentas Compartidas

Esta funcionalidad permite gestionar gastos grupales (viajes, piso compartido, regalos, etc.).

### 4.1. Crear Cuenta
Existen dos modalidades de reparto:

1.  **Reparto Equitativo:** El gasto se divide a partes iguales entre todos los miembros.
    ![Cuenta Equitativa](./imagenes/cuenta_equitativa.png)

2.  **Reparto Porcentual:** Usted define qué porcentaje del gasto asume cada usuario.
    > **Regla:** La suma de los porcentajes asignados a los miembros debe ser exactamente **100%**.

    ![Cuenta Porcentual](./imagenes/cuenta_porcentual.png)

### 4.2. Registrar Gasto Compartido
Dentro de la cuenta, pulse "Añadir Gasto". Deberá indicar quién ha pagado el importe; el sistema calculará automáticamente la deuda del resto de participantes.

![Gasto Compartido](./imagenes/registrar_gasto_cuenta_compartida.png)

---

## 5. Visualización y Estadísticas

Analice su salud financiera de forma visual.

* **Gráfica Circular (Pie Chart):** Vea rápidamente en qué categorías está gastando más dinero.
    ![Gráfica Circular](./imagenes/grafica_circular.png)

* **Vista Calendario:** Examine sus gastos distribuidos día a día en un calendario interactivo.
    ![Calendario](./imagenes/calendario.png)

---

## 6. Ayuda y Salir

* **Ayuda:** En el menú "Ayuda" > "Acerca de" encontrará información sobre la versión de la aplicación y los desarrolladores.
    ![Ayuda](./imagenes/ayuda.png)

* **Salir:** Al pulsar el botón "Salir" o cerrar la ventana, la aplicación guardará automáticamente todos sus datos de forma segura para la próxima sesión.

---

## 7. Modo Línea de Comandos (CLI)

Para usuarios avanzados o para realizar consultas rápidas sin interfaz gráfica, dispone de un modo consola.

Desde aquí puede listar, registrar y borrar gastos personales navegando por las opciones numéricas del menú.

![Modo Consola](./imagenes/linea_comandos.png)
