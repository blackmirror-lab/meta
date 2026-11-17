# MetaPunto 0.1 — primera versión para validar

Proyecto Android nativo, Java 17. Funciona sin permiso de Internet y conserva el original. Aún no validado en un teléfono. No es una herramienta de borrado forense universal.

## Compilar y obtener el APK con GitHub

1. Instala Git para Windows: https://git-scm.com/download/win
2. Crea un repositorio vacío en https://github.com/new llamado MetaPunto. Puede ser privado. No agregues README, licencia ni .gitignore.
3. Descomprime este ZIP completo. Ejecuta SUBIR_A_GITHUB.cmd dentro de MetaPunto. Pega la URL HTTPS de TU nuevo repositorio. Git puede abrir el navegador para iniciar sesión. No pegues contraseñas ni tokens en el chat.
4. En el repositorio abre Actions → Compilar APK → la ejecución más reciente. Si solicita habilitar Actions, habilítalo y usa Run workflow.
5. Espera a que termine en verde. En la página de esa ejecución, abajo, aparece Artifacts → MetaPunto-APK. Debes estar conectado a GitHub para descargar.
6. Descomprime el artefacto y copia app-debug.apk al teléfono. Ábrelo desde Archivos y permite instalar desde esa aplicación cuando Android lo solicite.

La compilación usa Gradle 8.9, Android Gradle Plugin 8.7.3 y SDK 35. El ZIP contiene .github/workflows/android.yml: no pierdas esa carpeta al subir. No hace falta instalar Android Studio para usar Actions. El APK es de prueba, firmado con clave debug del entorno de compilación; una compilación futura puede requerir desinstalar la anterior si la clave cambia. No se incluye firma de publicación.

## Uso

Seleccionar archivo → leer campos → crear copia limpia → guardar copia. Se procesa un archivo por vez, hasta 40 MB; Office expandido hasta 100 MB. La vista después de limpiar permite revisar los campos compatibles. Puede guardarse la copia sin tocar el original.

## Qué significa el punto

PDF: Author, Title, Subject, Keywords, Creator y Producer quedan con un punto. Se retiran fechas del diccionario Info y XMP referenciado en el árbol del documento. Se guarda completo, sin usar la edición incremental de ExifTool.

DOCX/XLSX/PPTX: propiedades de texto reconocidas de docProps se reemplazan por punto; otras propiedades, incluidas fechas y contadores, se eliminan. Propiedades personalizadas se convierten en texto con punto. Se preservan partes y relaciones del paquete. Comentarios, revisiones, miniaturas, imágenes, macros y adjuntos no se limpian.

JPEG: se retiran APP1–APP15 y comentarios; se agrega comentario con punto. PNG: se conservan bloques de imagen, transparencia y algunos de color; se agrega Comment con punto. WebP: se conservan bloques de imagen y transparencia, se retiran EXIF/XMP/ICC; no se agrega punto cuando no existe un contenedor de texto apropiado. No se fuerzan puntos en campos GPS, fechas ni estructuras binarias.

IMPORTANTE: quitar EXIF puede cambiar cómo se ve la orientación de una foto, y quitar perfiles ICC puede cambiar colores. Esta versión no normaliza orientación ni gestión de color. JPEG conserva el flujo comprimido desde el inicio del escaneo: no garantiza limpiar metadatos inusuales entre escaneos o al final. Para material sensible, usar un proceso adicional validado antes de compartir.

## Inspección y límites

Imágenes: metadata-extractor; muestra campos que reconoce, incluidos GPS si existen. PDF: Info y XMP a nivel documento. Office: XML de docProps. No se extraen todos los metadatos de todos los formatos. IP, correos y URL solo aparecerán si están en los campos leídos. No se deducen IP ni identidad. No se consulta Internet. No se verifica ni elimina universalmente IA, C2PA, marcas de agua o huellas.

Las firmas digitales pueden invalidarse. PDF cifrado y PNG/WebP animados no tienen limpieza habilitada. No admite limpiar DOC/XLS/PPT antiguos, HEIC, AVIF, vídeos o archivos arbitrarios. Un archivo no compatible nunca se presenta como limpiado.

Los archivos temporales quedan en la caché privada de la app hasta que Android la elimine o se borren sus datos. Los reportes contienen información sensible y se muestran solo en pantalla.

## Comprobación recomendada

Primero usa copias de prueba: una foto con GPS, PDF con autor, DOCX con autor y fecha. Comprueba que abren correctamente y revisa los metadatos con una segunda herramienta. La inspección interna no es una verificación independiente. No se promete borrar todos los rastros ocultos.

## Dependencias

metadata-extractor 2.19.0 (Apache 2.0), XMP Core transitivo (licencia BSD de Adobe), PdfBox-Android 2.0.27.0 (Apache 2.0). Revisar las licencias de sus dependencias antes de distribuir comercialmente.
