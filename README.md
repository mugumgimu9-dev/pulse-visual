# Pulse Visual - Modern Minecraft Client Mod

**Pulse Visual** - это современный визуальный клиент для Minecraft 1.16.5 на Fabric с расширенными возможностями кастомизации и анимацией.

## 🎯 Возможности

### Визуальные модули (Render)
- **Fullbright** - Освещение мира
- **ESP** - Подсветка сущностей
- **Tracers** - Линии к игрокам
- **NameTags** - Кастомизация ников
- **CustomCrosshair** - Пользовательский прицел
- **HUD** - Главный интерфейс с анимацией

### Боевые модули (Combat)
- **DamageParticles** - Эффекты урона
- **KillEffects** - Эффекты при убийстве
- **CriticalHit** - Крит эффекты
- **Totems** - Эффекты появления тотемов

### Утилиты (Utility)
- **Keystrokes** - Отображение нажатий
- **CPS** - Счётчик кликов
- **ArmorStatus** - Статус брони
- **PotionStatus** - Статус зелий

## 📋 Требования

- Java 8+
- Minecraft 1.16.5
- Fabric Loader 0.14.8+

## 🚀 Установка

1. Установите Fabric для 1.16.5
2. Скачайте JAR файл мода
3. Поместите в папку `mods`
4. Запустите игру

## 🛠️ Разработка

### Установка окружения
```bash
# Клонируем репозиторий
git clone https://github.com/mugumgimu9-dev/pulse-visual.git
cd pulse-visual

# Подготавливаем Gradle
./gradlew genSources

# Собираем проект
./gradlew build
```

### Структура проекта
```
src/main/java/com/pulsevisual/
├── PulseVisualMod.java           # Основной мод
├── client/
│   ├── PulseVisualClient.java    # Клиентская инициализация
│   ├── module/                   # Все модули
│   ├── manager/                  # Менеджеры (конфиг, кейбинды)
│   ├── gui/                      # GUI и ClickGUI
│   ├── render/                   # Утилиты рендера
│   ├── mixin/                    # Миксины для перехвата
│   └── event/                    # Система событий
```

## 📝 Создание нового модуля

```java
public class MyModule extends Module {
    public MyModule() {
        super("MyModule", "Описание", CATEGORY_RENDER);
    }

    @Override
    public void onEnable() { }

    @Override
    public void onDisable() { }

    @Override
    public void onUpdate() { }

    @Override
    public void onRender() { }
}
```

Затем зарегистрируйте в `ModuleManager.initializeModules()`:
```java
registerModule(new MyModule());
```

## ⚙️ Конфигурация

Все конфиги сохраняются в папке `.minecraft/pulse_visual/` в формате JSON.

## 🎮 Управление

- **P** - Открыть GUI
- **F** - Toggle Fullbright
- Все остальные кейбинды настраиваются в GUI

## 📄 Лицензия

MIT License - смотрите файл LICENSE

## 👨‍💻 Автор

mugumgimu9-dev

## 🤝 Контрибьютинг

Приветствуются Pull Requests! Для больших изменений сначала откройте Issue.

---

**Наслаждайтесь Pulse Visual!** ✨
