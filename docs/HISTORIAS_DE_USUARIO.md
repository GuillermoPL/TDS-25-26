# 📋 Especificación de Historias de Usuario
## Caso Práctico: Gestión de Gastos

**Facultad de Informática - Universidad de Murcia** **Asignatura:** Tecnologías de Desarrollo de Software (TDS)  
**Curso:** 2025/2026  
**Fecha:** 6 de noviembre de 2025

---

### 👥 Equipo de Desarrollo

| Nombre | Email |
| :--- | :--- |
| **Jorge Torralba Santa Cruz** | `jorge.torralbas@um.es` |
| **Juan Paredes Pardines** | `juan.paredesp@um.es` |
| **Guillermo Fulgencio Parra López** | `gf.parralopez@um.es` |

---

## 📑 Tabla de Contenidos
1. [Épica 1: Gestión de Gastos (CRUD)](#épica-1-gestión-de-gastos-crud)
2. [Épica 2: Visualización y Filtrado de Datos](#épica-2-visualización-y-filtrado-de-datos)
3. [Épica 3: Sistema de Alertas](#épica-3-sistema-de-alertas)
4. [Épica 4: Cuentas Compartidas](#épica-4-cuentas-compartidas)
5. [Épica 5: Importación](#épica-5-importación)

---

## Épica 1: Gestión de Gastos (CRUD)
[cite_start]Esta épica cubre el registro, modificación y borrado de gastos y categorías, además de la gestión de los gastos desde la línea de comandos[cite: 17, 18].

> ### 📝 HU 1.1: Registrar nuevo gasto (GUI)
> **Como** usuario de la aplicación,  
> **Quiero** registrar un nuevo gasto personal asociando cantidad, fecha y categoría desde la interfaz gráfica,  
> **Para** llevar un control sencillo de mis finanzas.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que estoy en la pantalla de registro de gastos, **Cuando** introduzco una cantidad válida (ej: 10.50), una fecha y selecciono una categoría existente (ej: Alimentación), **Entonces** el gasto se almacena de forma persistente (en el JSON) y los campos del formulario se limpian.
> - **Dado** que estoy en la pantalla de registro de gastos, **Cuando** intento guardar el gasto sin seleccionar una categoría, sin haber introducido una cantidad o haber introducido un importe incorrecto, **Entonces** el sistema muestra un mensaje de error y el gasto no se guarda.

> ### 🏷️ HU 1.2: Crear nueva categoría
> **Como** usuario,  
> **Quiero** crear nuevas categorías personalizadas (además de las predefinidas),  
> **Para** organizar mis gastos según mis necesidades específicas.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que estoy en la sección de Gestión de Categorías, **Cuando** introduzco un nombre para una nueva categoría (ej: Ocio) y pulso Guardar, **Entonces** la nueva categoría se persiste y aparece en la lista de categorías disponibles al registrar un gasto.
> - **Dado** que la categoría Alimentación ya existe, **Cuando** intento crear otra categoría con el nombre Alimentación, **Entonces** el sistema muestra un mensaje de error indicando que el nombre ya existe.

> ### ✏️ HU 1.3: Editar un gasto existente
> **Como** usuario,  
> **Quiero** poder editar los detalles (cantidad, fecha, categoría) de un gasto ya registrado,  
> **Para** corregir cualquier error en mis registros.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que he seleccionado un gasto existente de la lista de gastos, **Cuando** modifico su cantidad de 10€ a 15€ y guardo los cambios, **Entonces** el gasto se actualiza en la persistencia (JSON) y la lista muestra la cantidad 15€.

> ### 🗑️ HU 1.4: Borrar un gasto existente
> **Como** usuario,  
> **Quiero** poder borrar un gasto existente,  
> **Para** eliminar registros incorrectos o duplicados.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que he seleccionado un gasto de 15€ en Alimentación de la lista, **Cuando** pulso el botón Borrar y confirmo la acción, **Entonces** el gasto se elimina de forma persistente y desaparece de la lista de gastos.

> ### 💻 HU 1.5: Gestionar gastos desde la Línea de Comandos (CLI)
> **Como** usuario,  
> **Quiero** poder realizar operaciones básicas (registrar, modificar, borrar) desde una línea de comandos,  
> **Para** tener una alternativa rápida a la interfaz gráfica.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que he iniciado la aplicación en modo CLI, **Cuando** ejecuto el comando de registro, introduzco el importe, la fecha y la categoría, **Entonces** el sistema crea el nuevo gasto y lo persiste.
> - **Dado** que existe un gasto con ID gasto-123, **Cuando** ejecuto el comando de borrado e introduzco el ID del gasto a borrar, **Entonces** el sistema elimina el gasto de la persistencia.
> - **Dado** que la categoría Ropa no existe, **Cuando** creo/edito un gasto con dicha categoría, introduzco un importe inválido (menor o igual a cero) o una fecha incorrecta, **Entonces** la línea de comandos muestra un mensaje de error.

---

## Épica 2: Visualización y Filtrado de Datos
Esta épica cubre las diferentes formas de presentar la información y cómo filtrarla.

> ### 📋 HU 2.1: Consultar gastos en lista/tabla
> **Como** usuario,  
> **Quiero** consultar mis gastos registrados en un formato de tabla o lista,  
> **Para** tener una visión general y ordenada de mis movimientos.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que he registrado varios gastos, **Cuando** accedo a la pantalla principal de consulta, **Entonces** el sistema muestra una tabla con columnas (ej: Cantidad, Fecha, Categoría) de todos los gastos persistidos.

> ### 📊 HU 2.2: Visualizar gastos en gráficos
> **Como** usuario,  
> **Quiero** ver una representación gráfica (diagramas de barras y/o circulares) de mis gastos,  
> **Para** facilitar la comprensión de cómo se distribuyen mis gastos por categorías.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que tengo gastos registrados en Alimentación (50€) y Transporte (50€), **Cuando** selecciono la vista de Gráfico Circular, **Entonces** el sistema muestra un gráfico con dos mitades iguales, etiquetadas como Alimentación y Transporte.

> ### 📅 HU 2.3: Visualizar gastos en calendario (Avanzado)
> **Como** usuario,  
> **Quiero** ver mis gastos representados en una visualización de calendario,  
> **Para** identificar fácilmente en qué días concretos he realizado gastos.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que registré un gasto de 20€ en Ocio el día 5 de noviembre, **Cuando** abro la vista de Calendario y miro el mes de noviembre, **Entonces** el sistema (usando la librería *CalendarFX*) muestra una entrada en el día 5 que refleja dicho gasto.

> ### 🔍 HU 2.4: Filtrar gastos por múltiples criterios
> **Como** usuario,  
> **Quiero** filtrar los gastos por lista de meses, por intervalos de fechas personalizados, por una lista de categorías o por una combinación de estos,  
> **Para** analizar subconjuntos específicos de mi información.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que tengo gastos registrados a lo largo de varios meses, **Cuando** selecciono un intervalo de fechas personalizado (ej: 1 al 15 de noviembre) y aplico el filtro, **Entonces** la vista de gastos (tabla, gráficos) se actualiza para mostrar *únicamente* los gastos dentro de ese rango.
> - **Dado** que tengo gastos de Transporte en julio y agosto, y de Alimentación en julio, **Cuando** aplico un filtro combinado por meses (julio, agosto) Y por categoría (Transporte), **Entonces** la vista de gastos solo muestra los gastos de Transporte de julio y agosto, excluyendo los de Alimentación de julio.

---

## Épica 3: Sistema de Alertas
Cubre la configuración y notificación de límites de gasto.

> ### ⚙️ HU 3.1: Configurar alerta de gasto
> **Como** usuario,  
> **Quiero** establecer límites de gasto configurables (semanales, mensuales o por categoría),  
> **Para** recibir un aviso cuando esté gastando por encima de mi presupuesto.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que estoy en la configuración de alertas, **Cuando** creo una alerta de tipo semanal con un límite de 500 euros y la guardo, **Entonces** el sistema almacena esta regla de alerta (implementada con el **Patrón Estrategia**).
> - **Dado** que estoy en la configuración de alertas, **Cuando** creo una alerta mensual de 100 euros vinculada *específicamente* a la categoría Videojuegos, **Entonces** el sistema almacena esta regla de alerta específica.

> ### 🔔 HU 3.2: Recibir notificación de alerta superada
> **Como** usuario,  
> **Quiero** que el sistema genere una notificación legible cuando un nuevo gasto haga que supere un límite,  
> **Para** ser consciente inmediatamente de que he superado mi presupuesto.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que tengo una alerta de 100€/mes en Videojuegos y mi gasto actual en esa categoría es 90€, **Cuando** registro un nuevo gasto de 15€ en Videojuegos, **Entonces** el sistema genera una nueva notificación (ej: Límite superado: Has gastado 105€ de 100€ en Videojuegos este mes).

> ### 📜 HU 3.3: Revisar historial de notificaciones
> **Como** usuario,  
> **Quiero** poder revisar un historial de las notificaciones pasadas,  
> **Para** tener un registro de las veces que he superado mis límites.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que el sistema ha generado varias notificaciones de alerta en el pasado, **Cuando** accedo a la sección Historial de Notificaciones, **Entonces** puedo ver una lista de todas las notificaciones generadas, con su fecha y descripción.

---

## Épica 4: Cuentas Compartidas
[cite_start]Cubre la gestión de gastos entre varias personas en cuentas compartidas[cite: 99, 100].

> ### 👥 HU 4.1: Crear cuenta de gasto compartida
> **Como** usuario,  
> **Quiero** crear una cuenta de gasto compartida agrupando a varias personas (representadas por un nombre),  
> **Para** gestionar los gastos de un grupo con otras personas (ej: un piso compartido, un viaje).
>
> #### ✅ Criterios de Aceptación
> - **Dado** que estoy en la gestión de cuentas, **Cuando** creo una nueva cuenta e introduzco los nombres Raquel, Luis y María, **Entonces** el sistema crea la cuenta con esos 3 miembros, y el saldo inicial de cada uno es 0.
> - **Dado** que la cuenta con Raquel, Luis, María ya ha sido creada, **Cuando** intento añadir a Pedro a esa misma cuenta, **Entonces** el sistema no lo permite, ya que la lista de personas no puede ser modificada tras la creación.

> ### ➗ HU 4.2: Registrar gasto en cuenta compartida (Equitativa)
> **Como** miembro de una cuenta compartida,  
> **Quiero** registrar un gasto que he pagado yo o algún miembro,  
> **Para** que el sistema calcule automáticamente cuánto me deben o debemos los demás, asumiendo una distribución equitativa.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que existe una cuenta compartida con varios miembros, **Cuando** se registra un gasto, este está asociado a una persona, **Entonces** el resto de miembros le deben su parte proporcional del gasto.
> - **Dado** que existe una cuenta con Raquel, Luis y María (3 personas) con saldos a 0, **Cuando** registro un gasto de 30€ pagado por Raquel, **Entonces** el sistema actualiza y persiste los saldos: Raquel +20€, Luis -10€, María -10€.

> ### 🍰 HU 4.3: Registrar gasto en cuenta compartida (Porcentaje)
> **Como** miembro de una cuenta compartida,  
> **Quiero** que el sistema soporte una distribución de gastos por porcentaje definido,  
> **Para** reflejar acuerdos de gasto no equitativos.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que estoy creando una cuenta con Raquel, Luis y María, **Cuando** defino porcentajes de gasto (Raquel 40%, Luis 30%, María 30%) y guardo, **Entonces** la cuenta se crea con esta regla de distribución.
> - **Dado** que estoy creando una cuenta con Raquel, Luis y María, **Cuando** defino porcentajes de gasto que no suman 100% y guardo, **Entonces** la cuenta no se crea y se muestra un mensaje de error.
> - **Dado** que existe la cuenta anterior (40/30/30), **Cuando** registro un gasto de 100€ pagado por Raquel, **Entonces** el sistema actualiza los saldos: Raquel +60€, Luis -30€ y María -30€.

---

## Épica 5: Importación
Cubre los requisitos de la importación de ficheros.

> ### 📥 HU 5.1: Importar gastos desde fichero externo
> **Como** usuario,  
> **Quiero** importar un listado de gastos desde un fichero de texto plano (ej: de mi banco),  
> **Para** incorporar esos gastos en la aplicación sin introducirlos manualmente.
>
> #### ✅ Criterios de Aceptación
> - **Dado** que tengo un fichero de texto plano (como el de AulaVirtual) con un formato conocido, **Cuando** selecciono el fichero y el tipo de formato correspondiente (usando **Patrón Adaptador** y **Método Factoría**) e inicio la importación, **Entonces** el sistema lee el fichero, crea los registros de gasto correspondientes y los persiste.
