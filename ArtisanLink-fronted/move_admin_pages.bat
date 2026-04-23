@echo off
cd /d "%~dp0"
echo 正在移动B端页面到admin目录...

move "src\views\DashboardView.vue" "src\views\admin\DashboardView.vue" >nul 2>&1
if errorlevel 1 (echo DashboardView.vue 移动失败) else (echo DashboardView.vue 移动成功)

move "src\views\StaffView.vue" "src\views\admin\StaffView.vue" >nul 2>&1
if errorlevel 1 (echo StaffView.vue 移动失败) else (echo StaffView.vue 移动成功)

move "src\views\ScheduleView.vue" "src\views\admin\ScheduleView.vue" >nul 2>&1
if errorlevel 1 (echo ScheduleView.vue 移动失败) else (echo ScheduleView.vue 移动成功)

move "src\views\ServiceView.vue" "src\views\admin\ServiceView.vue" >nul 2>&1
if errorlevel 1 (echo ServiceView.vue 移动失败) else (echo ServiceView.vue 移动成功)

move "src\views\OrderView.vue" "src\views\admin\OrderView.vue" >nul 2>&1
if errorlevel 1 (echo OrderView.vue 移动失败) else (echo OrderView.vue 移动成功)

move "src\views\CustomerView.vue" "src\views\admin\CustomerView.vue" >nul 2>&1
if errorlevel 1 (echo CustomerView.vue 移动失败) else (echo CustomerView.vue 移动成功)

move "src\views\StatisticsView.vue" "src\views\admin\StatisticsView.vue" >nul 2>&1
if errorlevel 1 (echo StatisticsView.vue 移动失败) else (echo StatisticsView.vue 移动成功)

move "src\views\MessageView.vue" "src\views\admin\MessageView.vue" >nul 2>&1
if errorlevel 1 (echo MessageView.vue 移动失败) else (echo MessageView.vue 移动成功)

move "src\views\MerchantView.vue" "src\views\admin\MerchantView.vue" >nul 2>&1
if errorlevel 1 (echo MerchantView.vue 移动失败) else (echo MerchantView.vue 移动成功)

echo.
echo B端页面移动完成！
pause
