> [!WARNING]
The CLI tool is still in development, breaking changes and unexpected behaviors are expected.

---

Prism is a Java CLI utility and background daemon designed to extract, quantize, and compute optimal WCAG-compliant color palettes from images. It can be used as a standalone executable to analyze an image on-demand, or run as a background daemon that listens to a Unix Domain Socket (IPC) for events indicating an image (like a desktop wallpaper) has changed.

The extracted palette is output as a `.spec` file and is composed of five distinct tonal roles:
- **Lux:** Grayscale tones
- **Core:** Dominant color shade
- **Wave:** Secondary supporting shade
- **Flare:** Bright accent contrast
- **Spark:** Secondary accent sharpening

## Features
- **Color Quantization:** Reads an image, maps its pixels into 12-bit color buckets, and evaluates their frequency.
- **WCAG Contrast Optimization:** Generates palettes with optimized WCAG relative luminance and contrast ratios (`ContrastFinder`).
- **Daemon Mode:** Listens via a Unix Domain Socket (by default `/tmp/gbx.socket`) for live image changes, extracting paths from JSON-formatted IPC messages.
- **Caching Mechanism:** Holds a temporary cache of recently processed files for faster switching.
- **Exporting Options:** Can write color palettes in RGB or HEX format.

**Commands:**
- `-l`, `--listen` : Watch IPC socket to infer when a file is changed.
- `-s`, `--store` : Store given file location and directory of the current image.
- `-w`, `--write` : Write the fetched colors of an image to a system file.
- `-c`, `--cache` : Hold a temporary cache of the last processed files.
- `-a`, `--argument` : Pass a specific argument for execution.

**Subcommands:**
- `-d`, `--directory` : Specify a directory path.
- `-f`, `--file` : Specify a file path.
- `-a`, `--amount` : Define a cache or quantitative amount.
- `-t`, `--type` : Output format (e.g., `rgb` or `hex`).
- `interrupt` : Interrupt the target service.
- `reset` : Reset the target service state.
