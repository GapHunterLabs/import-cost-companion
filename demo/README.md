# Demo — Import Cost Companion

`node_modules/` acá es real (contenido de mentira, pero tamaños
reales en disco), con 3 paquetes de tamaño deliberadamente distinto:
`moment` (grande, con una carpeta `locale/` con 10 archivos),
`lodash` (mediano), `@acme/ui-kit` (chico, paquete con scope). `fs` no
tiene carpeta en `node_modules` a propósito -- es un módulo nativo de
Node, nunca se resuelve desde ahí.

## Pasos

1. Abrí `app.js`.
2. Click derecho (en cualquier parte del archivo, no hace falta
   seleccionar nada) → **Check Import Sizes**.
3. Debería abrirse `import-cost-report.md`.

## Resultado esperado

- `moment` con el tamaño más grande de los 3.
- `lodash` (aparece 2 veces: `lodash` y `lodash/debounce`) -- ambas
  líneas deberían mostrar el MISMO tamaño (el del paquete completo
  `lodash`, ya que `lodash/debounce` resuelve al mismo paquete).
- `@acme/ui-kit` con el tamaño más chico de los 3 (paquete con scope,
  confirma que la resolución de `@scope/pkg` funciona).
- `fs` → **"not found locally"**, no un tamaño inventado ni un 0 --
  este es el caso límite honesto a revisar con más atención.
- `./local-styles.css` (import relativo) NO debería aparecer en el
  reporte para nada -- se filtra antes, ni siquiera cuenta como
  import de `node_modules`.
- La notificación debería decir algo como "5 import(s) checked, 1 not
  found locally".

## Qué reportar

- ¿Los tamaños relativos tienen sentido (moment > lodash > ui-kit)?
- ¿`fs` se maneja de forma honesta (aviso de "not found"), no
  silenciosamente mal?
- ¿Correr la acción de nuevo sobrescribe el reporte limpio?
