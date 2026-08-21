# More Offhand Slots

More Offhand Slots is a NeoForge mod for Minecraft 1.21.1 that adds additional offhand slots and lets players cycle between them.

This branch also adds:

- An unbound-by-default **Stow Offhand Item** key that selects the first available empty offhand slot without changing the logical item order.
- A **Hide Slot Button** client option that opens the expanded SlotLib inventory by default and preserves compatible third-party inventory widgets.

## Requirements

- A 64-bit Java Development Kit (JDK) 21
- An internet connection for the first build
- Git, if cloning the repository instead of downloading a source archive

Verify the active Java version before building:

```text
java -version
```

The reported major version must be 21.

## Get the source

Clone this feature branch:

```text
git clone --branch feature/offhand-quality-of-life --single-branch https://github.com/Z-Siqi/moreoffhandslots.git
cd moreoffhandslots
```

Alternatively, download the branch as a ZIP from GitHub and extract it to a writable directory.

## Build

No system-wide Gradle installation is required. The included Gradle wrapper downloads the appropriate Gradle version automatically.

### Windows PowerShell

```text
.\gradlew.bat build
```

### Linux and macOS

```text
chmod +x gradlew
./gradlew build
```

A successful build ends with `BUILD SUCCESSFUL`. The distributable mod is generated at:

```text
build/libs/moreoffhandslots-neoforge-21.1.0.jar
```

Use the JAR without the `-slim` suffix when installing the mod.

## Reproducible build notes

The project currently builds against:

- Minecraft 1.21.1
- NeoForge 21.1.129
- Java 21
- Gradle 8.12 through the Gradle wrapper
- SlotLib `neoforge-21.1.0`, embedded in the distributable JAR by Jar-in-Jar

The first build downloads Minecraft artifacts, mappings, NeoForge, and other compile-time dependencies. Later builds reuse the local Gradle and project caches.

## Troubleshooting a corrupted Minecraft download

If the build fails in `neoFormExtractServer` or `create1.21.1ClientExtraJar` with the following error, a downloaded Minecraft JAR is incomplete or corrupted:

```text
java.util.zip.ZipException: zip END header not found
```

This is a download/cache problem rather than a Java compilation failure.

From the repository root, check the cached Minecraft files in PowerShell:

```powershell
$versionDir = Join-Path $PWD ".gradle\caches\minecraft\versions\1.21.1"

Get-ChildItem $versionDir -File |
    Select-Object Name, Length, LastWriteTime

Get-FileHash "$versionDir\server.jar" -Algorithm SHA1
Get-FileHash "$versionDir\client.jar" -Algorithm SHA1
```

The expected Minecraft 1.21.1 files are:

| File | Size in bytes | SHA-1 |
| --- | ---: | --- |
| `server.jar` | `51627615` | `59353FB40C36D304F2035D51E7D6E6BAA98DC05C` |
| `client.jar` | `26836906` | `30C73B1C5DA787909B2F73340419FDF13B9DEF88` |

If either file does not match, download fresh copies using the cached official Minecraft metadata:

```powershell
$versionDir = Join-Path $PWD ".gradle\caches\minecraft\versions\1.21.1"
$metadataPath = Join-Path $versionDir "metadata.json"
$metadata = Get-Content $metadataPath -Raw | ConvertFrom-Json

Invoke-WebRequest `
    -Uri $metadata.downloads.server.url `
    -OutFile (Join-Path $versionDir "server.jar")

Invoke-WebRequest `
    -Uri $metadata.downloads.client.url `
    -OutFile (Join-Path $versionDir "client.jar")
```

Verify the SHA-1 hashes again, then build without allowing the stale server-download task cache to restore a bad file:

```powershell
.\gradlew.bat build --no-build-cache -x cacheVersionExecutableServer1.21.1
```

If `metadata.json` has not been created yet, generate the version metadata first:

```powershell
.\gradlew.bat cacheVersionManifest1.21.1
```

## License

This project is licensed under LGPL-3.0-or-later. See [LICENSE](LICENSE).
