# ==============================================================================
# Ecossistema Integrados Fintech - Script de Inicializacao Robusta
# ==============================================================================

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " INICIANDO TODO O ECOSSISTEMA BANCARIO INTEGRADOS (HUB E SPOKE)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. Liberar portas antes de iniciar (evita erro de 'Port already in use')
$ports = @(8080, 3000, 3001, 3002)
Write-Host " Verificando e liberando portas ocupadas..." -ForegroundColor DarkGray
foreach ($port in $ports) {
    try {
        $conns = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
        if ($conns) {
            $pids = $conns | Select-Object -ExpandProperty OwningProcess -Unique
            foreach ($p in $pids) {
                if ($p -gt 0) {
                    Write-Host " -> Liberando porta $port (PID: $p)..." -ForegroundColor Yellow
                    Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
                }
            }
        }
    } catch {}
}
Start-Sleep -Seconds 1

# 2. Localizar diretorios com seguranca (evita problemas com acentuacao no caminho)
$IntegradosPath = $PSScriptRoot
$RootDir = (Get-Item $PSScriptRoot).Parent.FullName

$LaoBankDir = (Get-ChildItem -Path $RootDir -Directory -Filter "*consumerL*Bank" | Select-Object -First 1).FullName
$BackOfficeDir = (Get-ChildItem -Path $RootDir -Directory -Filter "*consumerBackOffice" | Select-Object -First 1).FullName
$NotificationDir = (Get-ChildItem -Path $RootDir -Directory -Filter "*consumerNotification" | Select-Object -First 1).FullName

Write-Host ""
Write-Host " Diretorios identificados:" -ForegroundColor DarkGray
Write-Host " -> Backend Core : $IntegradosPath" -ForegroundColor DarkGray
Write-Host " -> LaoBank B2C  : $LaoBankDir" -ForegroundColor DarkGray
Write-Host " -> BackOffice   : $BackOfficeDir" -ForegroundColor DarkGray
Write-Host " -> Notification : $NotificationDir" -ForegroundColor DarkGray
Write-Host ""

# 3. Iniciar API Backend Core (Spring Boot) na porta 8080
Write-Host "[1/4] Integrados Core API (Porta 8080)..." -ForegroundColor Green
Start-Process powershell -WorkingDirectory $IntegradosPath -ArgumentList "-NoExit", "-Command", "Write-Host 'Iniciando Core API Spring Boot...' -ForegroundColor Cyan; .\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=postgres'"

# 4. Iniciar consumerNotification na porta 3002
Write-Host "[2/4] consumerNotification (Porta 3002)..." -ForegroundColor Green
Start-Process powershell -WorkingDirectory $NotificationDir -ArgumentList "-NoExit", "-Command", "Write-Host 'Iniciando consumerNotification...' -ForegroundColor Yellow; npm run dev"

# 5. Iniciar consumerLaoBank na porta 3000
Write-Host "[3/4] consumerLaoBank B2C (Porta 3000)..." -ForegroundColor Green
Start-Process powershell -WorkingDirectory $LaoBankDir -ArgumentList "-NoExit", "-Command", "Write-Host 'Iniciando consumerLaoBank...' -ForegroundColor Green; npm run dev"

# 6. Iniciar consumerBackOffice na porta 3001
Write-Host "[4/4] consumerBackOffice B2B (Porta 3001)..." -ForegroundColor Green
Start-Process powershell -WorkingDirectory $BackOfficeDir -ArgumentList "-NoExit", "-Command", "Write-Host 'Iniciando consumerBackOffice...' -ForegroundColor Magenta; npm run dev"

Write-Host ""
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " TODOS OS 4 SERVICOS INICIADOS COM SUCESSO!" -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " B2C Internet Banking : http://localhost:3000" -ForegroundColor White
Write-Host " B2B Partner Hub      : http://localhost:3001" -ForegroundColor White
Write-Host " Notification Hub     : http://localhost:3002" -ForegroundColor White
Write-Host " Core Banking API     : http://localhost:8080" -ForegroundColor White
Write-Host " PostgreSQL           : localhost:5432" -ForegroundColor White
Write-Host "=================================================================" -ForegroundColor Cyan
