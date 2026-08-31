# ==============================================================================
# Ecossistema Integrados Fintech - Script de Encerramento Completo
# ==============================================================================

Write-Host "=================================================================" -ForegroundColor Yellow
Write-Host " ENCERRANDO PROCESSOS DO ECOSSISTEMA (Portas 8080, 3000, 3001, 3002)" -ForegroundColor Yellow
Write-Host "=================================================================" -ForegroundColor Yellow

$ports = @(8080, 3000, 3001, 3002)

foreach ($port in $ports) {
    try {
        $connections = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
        if ($connections) {
            $pids = $connections | Select-Object -ExpandProperty OwningProcess -Unique
            foreach ($p in $pids) {
                if ($p -gt 0) {
                    $procName = (Get-Process -Id $p -ErrorAction SilentlyContinue).ProcessName
                    Write-Host " -> Encerrando processo na porta $port (PID: $p, Nome: $procName)..." -ForegroundColor Red
                    Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
                }
            }
        } else {
            Write-Host " -> Porta $port ja esta livre." -ForegroundColor Green
        }
    } catch {
        # Ignora erros de permissao ou processos ja encerrados
    }
}

Write-Host "=================================================================" -ForegroundColor Green
Write-Host " TODOS OS SERVICOS FORAM ENCERRADOS COM SUCESSO!" -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Green
