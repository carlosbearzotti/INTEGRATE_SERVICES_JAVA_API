#!/usr/bin/env bash
# ==============================================================================
# Ecossistema Integrados Fintech - Script de Inicialização para Linux
# ==============================================================================

CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
WHITE='\033[1;37m'
GRAY='\033[0;90m'
NC='\033[0m'

echo -e "${CYAN}=================================================================${NC}"
echo -e "${CYAN} INICIANDO TODO O ECOSSISTEMA BANCÁRIO INTEGRADOS (LINUX HUB)${NC}"
echo -e "${CYAN}=================================================================${NC}"

# 1. Função para liberar porta
free_port() {
    local port=$1
    local pids=$(lsof -ti :$port 2>/dev/null || true)
    if [ -z "$pids" ]; then
        pids=$(ss -tlpn 2>/dev/null | grep ":$port " | grep -o 'pid=[0-9]*' | cut -d= -f2 || true)
    fi
    if [ -n "$pids" ]; then
        echo -e "${YELLOW} -> Liberando porta $port (PID: $pids)...${NC}"
        for pid in $pids; do
            kill -9 "$pid" 2>/dev/null || true
        done
    fi
}

# Liberar portas antes de iniciar (8080, 3000, 3001, 3002)
echo -e "${GRAY}Verificando e liberando portas ocupadas...${NC}"
for port in 8080 3000 3001 3002; do
    free_port $port
done
sleep 1

# 2. Localizar diretórios
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

INTEGRADOS_PATH="$SCRIPT_DIR"
LAOBANK_DIR=$(find "$ROOT_DIR" -maxdepth 1 -type d -iname "*BANK_CONSUMER_API*" -o -iname "*consumerL*Bank*" | head -n 1)
BACKOFFICE_DIR=$(find "$ROOT_DIR" -maxdepth 1 -type d -iname "*B2B_CONSUMER_API*" -o -iname "*consumerBackOffice*" | head -n 1)
NOTIFICATION_DIR=$(find "$ROOT_DIR" -maxdepth 1 -type d -iname "*NOTIFICATION_CONSUMER_API*" -o -iname "*consumerNotification*" | head -n 1)

echo ""
echo -e "${GRAY}Diretórios identificados:${NC}"
echo -e "${GRAY} -> Backend Core : $INTEGRADOS_PATH${NC}"
echo -e "${GRAY} -> LãoBank B2C  : $LAOBANK_DIR${NC}"
echo -e "${GRAY} -> BackOffice   : $BACKOFFICE_DIR${NC}"
echo -e "${GRAY} -> Notification : $NOTIFICATION_DIR${NC}"
echo ""

# Criar pasta de logs dentro do repositório
LOGS_DIR="$INTEGRADOS_PATH/.logs"
mkdir -p "$LOGS_DIR"

# 3. Iniciar consumerNotification na porta 3002 (Motor Go de alta performance)
if [ -d "$NOTIFICATION_DIR" ]; then
    echo -e "${GREEN}[1/4] Iniciando consumerNotification (Porta 3002 - Go Engine)...${NC}"
    nohup bash -c "cd '$NOTIFICATION_DIR' && go run main.go" > "$LOGS_DIR/notification.log" 2>&1 </dev/null &
    disown
    echo -e "${GRAY}      Logs: $LOGS_DIR/notification.log${NC}"
fi

# 4. Iniciar consumerLaoBank B2C na porta 3000
if [ -d "$LAOBANK_DIR" ]; then
    echo -e "${GREEN}[2/4] Iniciando consumerLãoBank B2C (Porta 3000)...${NC}"
    nohup bash -c "cd '$LAOBANK_DIR' && npx serve . -l 3000" > "$LOGS_DIR/laobank.log" 2>&1 </dev/null &
    disown
    echo -e "${GRAY}      Logs: $LOGS_DIR/laobank.log${NC}"
fi

# 5. Iniciar consumerBackOffice B2B na porta 3001
if [ -d "$BACKOFFICE_DIR" ]; then
    echo -e "${GREEN}[3/4] Iniciando consumerBackOffice B2B (Porta 3001)...${NC}"
    nohup bash -c "cd '$BACKOFFICE_DIR' && npx serve . -l 3001" > "$LOGS_DIR/backoffice.log" 2>&1 </dev/null &
    disown
    echo -e "${GRAY}      Logs: $LOGS_DIR/backoffice.log${NC}"
fi

# 6. Iniciar API Backend Core (Spring Boot) na porta 8080 (se Java/Maven disponível)
echo -e "${GREEN}[4/4] Verificando Core API Spring Boot (Porta 8080)...${NC}"
if command -v java >/dev/null 2>&1; then
    echo -e "${CYAN}      Iniciando Core API Spring Boot com perfil 'postgres'...${NC}"
    if [ -f "$INTEGRADOS_PATH/mvnw" ]; then
        nohup bash -c "cd '$INTEGRADOS_PATH' && ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres" > "$LOGS_DIR/core-api.log" 2>&1 </dev/null &
        disown
    elif command -v mvn >/dev/null 2>&1; then
        nohup bash -c "cd '$INTEGRADOS_PATH' && mvn spring-boot:run -Dspring-boot.run.profiles=postgres" > "$LOGS_DIR/core-api.log" 2>&1 </dev/null &
        disown
    fi
else
    echo -e "${YELLOW}      ⚠️ Java não detectado no ambiente. Para rodar o Core API, instale o OpenJDK 17+.${NC}"
fi

# Aguardar subida dos serviços
sleep 3

echo ""
echo -e "${CYAN}=================================================================${NC}"
echo -e "${GREEN} SERVIÇOS DO ECOSSISTEMA ATIVOS EM SEGUNDO PLANO!${NC}"
echo -e "${CYAN}=================================================================${NC}"
echo -e "${WHITE} 🏦 B2C Internet Banking : http://localhost:3000${NC}"
echo -e "${WHITE} 🏢 B2B Partner Hub      : http://localhost:3001${NC}"
echo -e "${WHITE} 📬 Notification Hub (Go): http://localhost:3002${NC}"
echo -e "${WHITE} ⚙️  Core Banking API     : http://localhost:8080${NC}"
echo -e "${GRAY} 📁 Logs disponíveis em  : $LOGS_DIR/${NC}"
echo -e "${CYAN}=================================================================${NC}"
echo -e "${GRAY} Para verificar o status execute : ./status-all.sh${NC}"
echo -e "${GRAY} Para parar todos os serviços    : ./stop-all.sh${NC}"
