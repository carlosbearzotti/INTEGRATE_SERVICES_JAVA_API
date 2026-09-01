#!/usr/bin/env bash
# ==============================================================================
# Ecossistema Integrados Fintech - Script de Encerramento para Linux
# ==============================================================================

CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
GRAY='\033[0;90m'
NC='\033[0m'

echo -e "${CYAN}=================================================================${NC}"
echo -e "${CYAN} ENCERRANDO TODOS OS SERVIÇOS DO ECOSSISTEMA INTEGRADOS${NC}"
echo -e "${CYAN}=================================================================${NC}"

ports=(8080 3000 3001 3002)

for port in "${ports[@]}"; do
    pids=$(lsof -ti :$port 2>/dev/null || true)
    if [ -z "$pids" ]; then
        pids=$(ss -tlpn 2>/dev/null | grep ":$port " | grep -o 'pid=[0-9]*' | cut -d= -f2 || true)
    fi
    if [ -n "$pids" ]; then
        echo -e "${YELLOW} -> Encerrando processos na porta $port (PID: $pids)...${NC}"
        for pid in $pids; do
            kill -15 "$pid" 2>/dev/null || true
            sleep 0.5
            kill -9 "$pid" 2>/dev/null || true
        done
        echo -e "${GREEN}    Porta $port liberada com sucesso.${NC}"
    else
        echo -e "${GRAY} -> Porta $port já está livre.${NC}"
    fi
done

echo ""
echo -e "${GREEN}=================================================================${NC}"
echo -e "${GREEN} TODOS OS SERVIÇOS FORAM ENCERRADOS COM SUCESSO!${NC}"
echo -e "${GREEN}=================================================================${NC}"
