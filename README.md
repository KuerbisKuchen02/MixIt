# MixIt

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

## 🧭 Overview
<p align="center">
  <img src="https://raw.githubusercontent.com/KuerbisKuchen02/MixIt/83ec42db4d55902d371e2feaebd5e7395d1b439e/app/src/main/res/drawable/ic_mix_it.png" width="128" style="margin-right: 20px; float: left;">
</p>
<p align="center">
    MixIt is a native android app inspired by the popular game InfiniteCraft. You start with the four basic elements water, earth, fire and air and your task is to combine them into new unique elements.
</p>


## 🚀 Features
✅ Discover new words inside an endless free play mode.  
✅ Try to reach a given target word as fast as you can and in the least amount of time.  
✅ Got something else to do? No problem! The game saves your progress for you.  
✅ Stay motivated by unlocking 15 unique achievements!  
✅ Play the game in dark- or light mode.  
✅ Reset your progress if you want to start over.  
✅ Full i18n (currently supports english and german)


## ⚙️ Installation
### 🔎 Prerequisites
> Please make sure you have Java and an android SDK installed before building the application.  
>  - The minimal SDK version is 24  
>  - The target SDK version is 36

### 🎯 Process

1. Clone the repository to your local machine. Open a terminal of your choice and type:
```bash
git clone https://github.com/KuerbisKuchen02/MixIt.git
```

2. Add an android SDK and OpenAI key in a file named `local.properties` in the root of your project directory. Your file should look like this:
```properties
sdk.dir=your/path/to/android_sdk
API_KEY=sk-proj-XXXXXXXXXXXXXXXXXXXX-XXXXXXXXXXXXXXXXXXXX_XXXXXXXXXXXXXXXXXXXX-XXXXXXXXXXXXXXXXXXXX-XXXXXXXXXXXXXXXXXXXX
```

3. Build the app APK
```bash
./gradlew app:assemble
```

4. Install the application either as debug or release version  
- For the debug Sdk type:
```bash
# Install debug sdk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
