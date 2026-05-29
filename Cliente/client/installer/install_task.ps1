param(
    # Ruta al ejecutable — ajusta según donde instales la app
    [string]$ExePath = "$env:LOCALAPPDATA\ESAIL IT\client.exe"
)

$taskName = "ESAIL IT Alarma"

# --- Eliminar tarea anterior si existe ---
$existente = Get-ScheduledTask -TaskName $taskName -ErrorAction SilentlyContinue
if ($existente) {
    Unregister-ScheduledTask -TaskName $taskName -Confirm:$false
    Write-Host "Tarea anterior eliminada"
}

# --- Definir la acción: ejecutar el .exe con --minimized ---
$action = New-ScheduledTaskAction `
    -Execute $ExePath `
    -Argument "--minimized"

# --- Disparador: al hacer login, con 2 minutos de espera ---
# PT2M = 2 minutos en formato ISO 8601
$trigger = New-ScheduledTaskTrigger `
    -AtLogOn `
    -Delay "PT2M"

# --- Configuración: sin límite de tiempo, funciona con batería ---
$settings = New-ScheduledTaskSettingsSet `
    -AllowStartIfOnBatteries `
    -DontStopIfGoingOnBatteries `
    -ExecutionTimeLimit 0 `
    -RestartCount 3 `
    -RestartInterval (New-TimeSpan -Minutes 1)

# --- Registrar la tarea para el usuario actual ---
Register-ScheduledTask `
    -TaskName $taskName `
    -Action $action `
    -Trigger $trigger `
    -Settings $settings `
    -RunLevel Highest `
    -Force

Write-Host "   Tarea '$taskName' instalada correctamente"
Write-Host "   Ejecutable: $ExePath"
Write-Host "   Arrancará 2 minutos después de cada login de Windows"