$listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($listener) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/" -UseBasicParsing -TimeoutSec 3
    }
    catch {
        $response = $null
    }

    if ($response -and $response.Content -match '<title>FoodShare') {
        Write-Host "FoodShare is already running at http://localhost:8080."
        return
    }

    $process = Get-Process -Id $listener.OwningProcess -ErrorAction SilentlyContinue
    $processName = if ($process) { $process.ProcessName } else { "PID $($listener.OwningProcess)" }
    throw "Port 8080 is already in use by '$processName' (PID $($listener.OwningProcess)). Stop it or change server.port before launching FoodShare."
}

$securePassword = Read-Host "MySQL root password" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    mvn -o spring-boot:run
    if ($LASTEXITCODE -ne 0) {
        throw "Maven exited with code $LASTEXITCODE."
    }
}
finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
}