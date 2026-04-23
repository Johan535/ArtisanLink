# PowerShell脚本：移动B端页面到admin目录
$ErrorActionPreference = "Continue"

Write-Host "正在移动B端页面到admin目录..." -ForegroundColor Green

$files = @(
    "DashboardView.vue",
    "StaffView.vue", 
    "ScheduleView.vue",
    "ServiceView.vue",
    "OrderView.vue",
    "CustomerView.vue",
    "StatisticsView.vue",
    "MessageView.vue",
    "MerchantView.vue"
)

$sourceDir = "src\views"
$targetDir = "src\views\admin"

foreach ($file in $files) {
    $sourcePath = Join-Path $sourceDir $file
    $targetPath = Join-Path $targetDir $file
    
    if (Test-Path $sourcePath) {
        try {
            Move-Item -Path $sourcePath -Destination $targetPath -Force
            Write-Host "✓ $file 移动成功" -ForegroundColor Green
        } catch {
            Write-Host "✗ $file 移动失败: $_" -ForegroundColor Red
        }
    } else {
        Write-Host "- $file 源文件不存在，跳过" -ForegroundColor Yellow
    }
}

Write-Host "`nB端页面移动完成！" -ForegroundColor Cyan
