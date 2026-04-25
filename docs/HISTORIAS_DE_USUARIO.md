# Especificación de Historias de Usuario
## Caso Práctico: Gestión de Gastos

**Facultad de Informática — Universidad de Murcia**
**Asignatura:** Tecnologías de Desarrollo de Software (TDS)
**Curso:** 2025/2026
**Fecha:** 25 de abril de 2026

---

### Equipo de Desarrollo

| Nombre | Email |
| :--- | :--- |
| Jorge Torralba Santa Cruz | `jorge.torralbas@um.es` |
| Juan Paredes Pardines | `juan.paredesp@um.es` |
| Guillermo Fulgencio Parra López | `gf.parralopez@um.es` |

---

## Tabla de Contenidos

1. [Épica 1: Gestión de Gastos (CRUD)](#épica-1-gestión-de-gastos-crud)
2. [Épica 2: Visualización y Filtrado de Datos](#épica-2-visualización-y-filtrado-de-datos)
3. [Épica 3: Sistema de Alertas](#épica-3-sistema-de-alertas)
4. [Épica 4: Cuentas Compartidas](#épica-4-cuentas-compartidas)
5. [Épica 5: Importación de Datos Externos](#épica-5-importación-de-datos-externos)
6. [Mejoras generales aplicadas](#mejoras-generales-aplicadas)

---

## Épica 1: Gestión de Gastos (CRUD)

Esta épica recoge el registro, modificación y borrado de gastos, la creación de categorías personalizadas y la gestión de gastos desde la línea de comandos.

### HU 1.1: Registrar un nuevo gasto desde la interfaz gráfica

- **Como** usuario de la aplicación,
- **Quiero** registrar un nuevo gasto indicando cantidad, fecha y categoría desde la interfaz gráfica,
- **Para** llevar un control sencillo de mis finanzas personales.

**Criterios de aceptación:**

1. *Registro con datos válidos*
   - **Dado que** estoy en la pantalla de registro de gastos,
   - **Cuando** introduzco una cantidad positiva (por ejemplo, 10,50 €), una fecha y selecciono una categoría existente (por ejemplo, Alimentación),
   - **Entonces** el gasto se almacena de forma persistente y los campos del formulario se limpian.

2. *Registro con datos inválidos*
   - **Dado que** estoy en la pantalla de registro de gastos,
   - **Cuando** intento guardar sin seleccionar categoría, sin introducir cantidad o con un importe no válido (por ejemplo, menor o igual a cero),
   - **Entonces** el sistema muestra un mensaje de error y el gasto no se guarda.

---

### HU 1.2: Crear una nueva categoría

- **Como** usuario,
- **Quiero** crear nuevas categorías personalizadas, además de las predefinidas (alimentación, transporte, entretenimiento…),
- **Para** organizar mis gastos según mis necesidades.

**Criterios de aceptación:**

1. *Creación de una categoría nueva*
   - **Dado que** estoy en la sección de gestión de categorías,
   - **Cuando** introduzco un nombre nuevo (por ejemplo, Ocio) y pulso Guardar,
   - **Entonces** la categoría se persiste y aparece en la lista disponible al registrar un gasto.

2. *Categoría duplicada*
   - **Dado que** la categoría Alimentación ya existe,
   - **Cuando** intento crear otra con el mismo nombre,
   - **Entonces** el sistema muestra un mensaje de error indicando que el nombre ya existe.

---

### HU 1.3: Editar un gasto existente

- **Como** usuario,
- **Quiero** editar los detalles (cantidad, fecha o categoría) de un gasto ya registrado,
- **Para** corregir posibles errores en mis registros.

**Criterios de aceptación:**

1. *Edición correcta*
   - **Dado que** he seleccionado un gasto existente de la lista,
   - **Cuando** modifico alguno de sus campos (por ejemplo, cambio la cantidad de 10 € a 15 €) y guardo los cambios,
   - **Entonces** el gasto se actualiza de forma persistente y la lista refleja los nuevos valores.

---

### HU 1.4: Borrar un gasto existente

- **Como** usuario,
- **Quiero** borrar un gasto registrado,
- **Para** eliminar registros incorrectos o duplicados.

**Criterios de aceptación:**

1. *Borrado confirmado*
   - **Dado que** he seleccionado un gasto de la lista,
   - **Cuando** pulso el botón de borrar y confirmo la acción,
   - **Entonces** el gasto se elimina de forma persistente y desaparece de la lista.

---

### HU 1.5: Gestionar gastos desde la línea de comandos

- **Como** usuario,
- **Quiero** poder registrar, modificar y borrar gastos desde una línea de comandos,
- **Para** disponer de una alternativa rápida a la interfaz gráfica.

**Criterios de aceptación:**

1. *Registro por CLI*
   - **Dado que** he iniciado la aplicación en modo línea de comandos,
   - **Cuando** ejecuto el comando de registro indicando importe, fecha y categoría válidos,
   - **Entonces** el sistema crea el nuevo gasto y lo persiste.

2. *Modificación y borrado por CLI*
   - **Dado que** existe un gasto identificable (por ejemplo, con identificador `gasto-123`),
   - **Cuando** ejecuto el comando de modificación o borrado indicando dicho identificador,
   - **Entonces** el sistema actualiza o elimina el gasto y persiste el cambio.

3. *Datos inválidos por CLI*
   - **Dado que** introduzco un importe no válido (menor o igual a cero), una fecha incorrecta o una categoría inexistente,
   - **Cuando** intento ejecutar el comando,
   - **Entonces** la línea de comandos muestra un mensaje de error y la operación no se realiza.

---

## Épica 2: Visualización y Filtrado de Datos

Esta épica describe las distintas formas de presentar la información registrada y los criterios de filtrado disponibles.

### HU 2.1: Consultar gastos en formato de tabla o lista

- **Como** usuario,
- **Quiero** consultar mis gastos registrados en formato de tabla o lista,
- **Para** tener una visión general y ordenada de mis movimientos.

**Criterios de aceptación:**

1. *Visualización tabular*
   - **Dado que** he registrado varios gastos,
   - **Cuando** accedo a la pantalla principal de consulta,
   - **Entonces** el sistema muestra una tabla con todos los gastos persistidos, indicando al menos cantidad, fecha y categoría.

---

### HU 2.2: Visualizar gastos en gráficos

- **Como** usuario,
- **Quiero** ver una representación gráfica de mis gastos mediante diagramas de barras o circulares,
- **Para** comprender de forma visual cómo se distribuyen mis gastos por categorías.

**Criterios de aceptación:**

1. *Gráfico circular por categorías*
   - **Dado que** tengo gastos registrados de 50 € en Alimentación y 50 € en Transporte,
   - **Cuando** selecciono la vista de gráfico circular,
   - **Entonces** el sistema muestra un gráfico con dos sectores iguales etiquetados como Alimentación y Transporte.

---

### HU 2.3: Visualizar gastos en una vista de calendario

- **Como** usuario,
- **Quiero** ver mis gastos representados en una vista de calendario,
- **Para** identificar fácilmente los días concretos en los que he realizado gastos.

**Criterios de aceptación:**

1. *Entrada en el calendario*
   - **Dado que** registré un gasto de 20 € en Ocio el 5 de noviembre,
   - **Cuando** abro la vista de calendario sobre el mes de noviembre,
   - **Entonces** el sistema muestra una entrada en el día 5 que refleja dicho gasto.

---

### HU 2.4: Filtrar gastos por múltiples criterios

- **Como** usuario,
- **Quiero** filtrar los gastos por una lista de meses, por intervalos de fechas, por una lista de categorías o por una combinación de estos criterios,
- **Para** analizar subconjuntos concretos de mi información.

**Criterios de aceptación:**

1. *Filtro por intervalo de fechas*
   - **Dado que** tengo gastos registrados a lo largo de varios meses,
   - **Cuando** selecciono un intervalo de fechas (por ejemplo, del 1 al 15 de noviembre) y aplico el filtro,
   - **Entonces** la vista de gastos se actualiza para mostrar únicamente los gastos comprendidos en ese rango.

2. *Filtro combinado por meses y categoría*
   - **Dado que** tengo gastos de Transporte en julio y agosto, y de Alimentación en julio,
   - **Cuando** aplico un filtro por meses (julio, agosto) y por categoría (Transporte),
   - **Entonces** la vista solo muestra los gastos de Transporte en julio y agosto, excluyendo los de Alimentación de julio.

---

## Épica 3: Sistema de Alertas

Esta épica cubre la configuración de límites de gasto, la generación de notificaciones cuando se superan y la consulta del historial.

### HU 3.1: Configurar una alerta de gasto

- **Como** usuario,
- **Quiero** establecer límites de gasto configurables, ya sea de forma semanal o mensual y opcionalmente vinculados a una categoría,
- **Para** recibir un aviso cuando supere mi presupuesto.

**Criterios de aceptación:**

1. *Alerta semanal general*
   - **Dado que** estoy en la configuración de alertas,
   - **Cuando** creo una alerta semanal con un límite de 500 € y la guardo,
   - **Entonces** el sistema almacena la regla de alerta de forma persistente.

2. *Alerta mensual vinculada a una categoría*
   - **Dado que** estoy en la configuración de alertas,
   - **Cuando** creo una alerta mensual de 100 € vinculada a la categoría Videojuegos,
   - **Entonces** el sistema almacena la regla específica para esa categoría.

---

### HU 3.2: Recibir una notificación al superar un límite

- **Como** usuario,
- **Quiero** que el sistema genere una notificación cuando un nuevo gasto haga que supere uno de mis límites configurados,
- **Para** ser consciente de inmediato de que he excedido mi presupuesto.

**Criterios de aceptación:**

1. *Notificación al superar el límite*
   - **Dado que** tengo una alerta de 100 €/mes en Videojuegos y el gasto acumulado del mes en esa categoría es de 90 €,
   - **Cuando** registro un nuevo gasto de 15 € en Videojuegos,
   - **Entonces** el sistema genera una notificación legible indicando que se ha superado el límite (por ejemplo: «Has gastado 105 € de 100 € en Videojuegos este mes»).

---

### HU 3.3: Revisar el historial de notificaciones

- **Como** usuario,
- **Quiero** revisar un historial con las notificaciones pasadas,
- **Para** llevar un registro de las veces que he superado mis límites.

**Criterios de aceptación:**

1. *Consulta del historial*
   - **Dado que** el sistema ha generado notificaciones de alerta en el pasado,
   - **Cuando** accedo a la sección Historial de Notificaciones,
   - **Entonces** veo una lista con todas las notificaciones generadas, junto con su fecha y descripción.

---

## Épica 4: Cuentas Compartidas

Esta épica describe el funcionamiento de las cuentas de gasto compartidas entre varias personas, con distribución equitativa o por porcentaje.

### HU 4.1: Crear una cuenta de gasto compartida

- **Como** usuario,
- **Quiero** crear una cuenta de gasto compartida que agrupe a varias personas (representadas por un nombre),
- **Para** gestionar gastos de un grupo, como un piso compartido o un viaje.

**Criterios de aceptación:**

1. *Creación de la cuenta*
   - **Dado que** estoy en la gestión de cuentas,
   - **Cuando** creo una nueva cuenta e introduzco los nombres de los miembros (por ejemplo, Raquel, Luis y María),
   - **Entonces** el sistema crea la cuenta con esos miembros y establece el saldo inicial de cada uno a 0.

2. *Lista de miembros inmutable*
   - **Dado que** la cuenta ya ha sido creada,
   - **Cuando** intento añadir o eliminar a una persona de la lista de miembros,
   - **Entonces** el sistema no permite la operación, ya que la lista de personas no puede modificarse tras la creación.

---

### HU 4.2: Registrar un gasto con distribución equitativa

- **Como** miembro de una cuenta compartida con distribución equitativa,
- **Quiero** registrar un gasto pagado por uno de los miembros,
- **Para** que el sistema calcule automáticamente cuánto le deben los demás.

**Criterios de aceptación:**

1. *Reparto equitativo*
   - **Dado que** existe una cuenta compartida formada por Raquel, Luis y María, todos con saldo 0,
   - **Cuando** registro un gasto de 30 € pagado por Raquel,
   - **Entonces** el sistema actualiza y persiste los saldos: Raquel +20 €, Luis −10 € y María −10 €.

---

### HU 4.3: Crear y usar una cuenta compartida con distribución por porcentaje

- **Como** usuario,
- **Quiero** crear una cuenta compartida indicando el porcentaje de gasto que asume cada miembro,
- **Para** reflejar acuerdos de gasto que no son equitativos.

**Criterios de aceptación:**

1. *Creación con porcentajes válidos*
   - **Dado que** estoy creando una cuenta con Raquel, Luis y María,
   - **Cuando** defino los porcentajes (Raquel 40 %, Luis 30 %, María 30 %) y guardo,
   - **Entonces** la cuenta se crea con la regla de distribución indicada.

2. *Porcentajes inválidos*
   - **Dado que** estoy creando una cuenta con varios miembros,
   - **Cuando** defino unos porcentajes que no suman 100 %,
   - **Entonces** la cuenta no se crea y el sistema muestra un mensaje de error.

3. *Reparto por porcentaje*
   - **Dado que** existe una cuenta con Raquel (40 %), Luis (30 %) y María (30 %),
   - **Cuando** se registra un gasto de 100 € pagado por Raquel,
   - **Entonces** el sistema actualiza los saldos: Raquel +60 €, Luis −30 € y María −30 €.

---

## Épica 5: Importación de Datos Externos

Esta épica recoge la importación de datos de gasto desde ficheros externos.

### HU 5.1: Importar gastos desde un fichero externo

- **Como** usuario,
- **Quiero** importar un listado de gastos desde un fichero de texto plano (por ejemplo, generado por mi entidad bancaria),
- **Para** incorporar esos gastos a la aplicación sin tener que introducirlos manualmente.

**Criterios de aceptación:**

1. *Importación correcta*
   - **Dado que** dispongo de un fichero de texto plano con un formato conocido (como el de ejemplo proporcionado en Aula Virtual),
   - **Cuando** selecciono el fichero, indico su formato e inicio la importación,
   - **Entonces** el sistema lee el fichero, crea los registros de gasto correspondientes y los persiste.

2. *Soporte para distintos formatos*
   - **Dado que** la aplicación debe estar preparada para distintos formatos de importación,
   - **Cuando** se necesita incorporar un nuevo formato de fichero,
   - **Entonces** el sistema permite añadirlo sin alterar el funcionamiento de los formatos ya soportados.

---


