@echo off
chcp 65001 > nul
echo =================================================================
echo  🏦 INICIANDO TODO O ECOSSISTEMA INTEGRADOS (HUB & SPOKE)
echo =================================================================

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-all.ps1"
