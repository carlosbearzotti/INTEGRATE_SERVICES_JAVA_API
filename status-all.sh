#!/usr/bin/env bash
# ==============================================================================
# Ecossistema Integrados Fintech - Script de Status dos Serviços (Linux)
# ==============================================================================

CYAN='\033[0;36m'
GREEN='\033[0;32m'
RED='\033[0;31m'
WHITE='\033[1;37m'
GRAY='\033[0;90m'
NC='\033[0m'

echo -e "${CYAN}=================================================================${NC}"
echo -e "${CYAN} STATUS DO ECOSSISTEMA BANCÁRIO INTEGRADOS${NC}"
echo -e "${CYAN}=================================================================${NC}"

check_service() {
    local name="$1"
    local port="$2"
    local url="$3"

    local pids=$(lsof -ti :$port 2>/dev/null || true)
    if [ -z "$pids" ]; then
        pids=$(ss -tlpn 2>/dev/null | grep ":$port " | grep -o 'pid=[0-9]*' | cut -d= -f2 || true)
    fi

    if [ -n "$pids" ]; then
        echo -e "${GREEN}● ONLINE ${NC} | ${WHITE}${name}${NC} (Porta ${port}, PID: ${pids})"
        echo -e "          ↳ ${url}"
    else
        echo -e "${RED}○ OFFLINE${NC} | ${GRAY}${name} (Porta ${port}) - Parado${NC}"
    fi
}

check_service "B2C Internet Banking  (LãoBank)" 3000 "http://localhost:3000"
check_service "B2B Partner Hub       (BackOffice)" 3001 "http://localhost:3001"
check_service "Notification Hub (Go Engine)" 3002 "http://localhost:3002"
check_service "Core Banking API      (Spring Boot)" 8080 "http://localhost:8080"

echo -e "${CYAN}=================================================================${NC}"
