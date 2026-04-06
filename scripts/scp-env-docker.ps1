<#
.SYNOPSIS
  로컬 프로젝트 루트의 .env.docker 를 Lightsail(Ubuntu) 의 프로젝트 경로로 scp 전송합니다.

.DESCRIPTION
  Windows PowerShell 에서만 실행하세요. OpenSSH 클라이언트(scp)가 설치되어 있어야 합니다.

.EXAMPLE
  .\scripts\scp-env-docker.ps1 -PemPath "C:\keys\LightsailDefaultKey-ap-northeast-2.pem" -ServerHost "13.124.250.113"

.EXAMPLE
  .\scripts\scp-env-docker.ps1 -PemPath "C:\keys\key.pem" -ServerHost "13.124.250.113" -User "ubuntu" -RemoteDir "/home/ubuntu/prjt-backend-operational"
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, HelpMessage = "Lightsail PEM 파일 전체 경로")]
    [string] $PemPath,

    [Parameter(Mandatory = $true, HelpMessage = "서버 공인 IP 또는 DNS")]
    [string] $ServerHost,

    [Parameter(Mandatory = $false)]
    [string] $User = "ubuntu",

    [Parameter(Mandatory = $false, HelpMessage = "원격 서버에서 docker-compose 가 있는 디렉터리")]
    [string] $RemoteDir = "/home/ubuntu/prjt-backend-operational"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# 스크립트: <프로젝트루트>/scripts/scp-env-docker.ps1
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$LocalEnv = Join-Path $ProjectRoot ".env.docker"

if (-not (Test-Path -LiteralPath $LocalEnv)) {
    throw "로컬 파일이 없습니다: $LocalEnv`n.example 을 복사해 채운 뒤 다시 실행하세요."
}

if (-not (Test-Path -LiteralPath $PemPath)) {
    throw "PEM 파일이 없습니다: $PemPath"
}

$RemoteDirNormalized = $RemoteDir.TrimEnd("/")
$RemoteSpec = "${User}@${ServerHost}:${RemoteDirNormalized}/.env.docker"

Write-Host "로컬: $LocalEnv"
Write-Host "원격: $RemoteSpec"
Write-Host ""

& scp -i $PemPath $LocalEnv $RemoteSpec
if ($LASTEXITCODE -ne 0) {
    throw "scp 종료 코드: $LASTEXITCODE"
}

Write-Host ""
Write-Host "전송 완료. 서버에서 예: cd $RemoteDirNormalized && docker compose up -d"
