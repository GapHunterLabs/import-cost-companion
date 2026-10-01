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
- `lodash` en UNA sola línea, nombrando sus dos imports (`lodash` y
  `lodash/debounce` son el mismo paquete, así que se cuenta una vez).
- `@acme/ui-kit` con el tamaño más chico de los 3 (paquete con scope,
  confirma que la resolución de `@scope/pkg` funciona).
- `fs` **no** aparece como "not found locally": es un módulo nativo de
  Node, así que va en la línea aparte de built-ins al final del
  reporte, sin tamaño.
