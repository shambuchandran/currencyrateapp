# Currency Converter App

## Overview
The **Currency Converter App** is a cross-platform solution designed to provide users with up-to-date exchange rates and a seamless way to convert between different currencies. Built using **Jetpack Compose Multiplatform (CMP)**, it delivers a **native-like experience** across Android and iOS from a single codebase.

## Features

### 🔄 Real-time Exchange Rates
- Fetches the latest exchange rates from a **reliable API** using **Ktor**, a lightweight and flexible multiplatform HTTP client.
- Ensures accurate and up-to-date currency conversions.

### 📶 Offline Access & Data Persistence
- Stores exchange rate data and user preferences **locally** using **MongoDB Realm**.
- Provides seamless synchronization and offline capabilities.
- Allows users to access and convert currencies **even without an internet connection**.

### 🏗️ Dependency Injection
- Utilizes **Koin**, a lightweight **dependency injection** framework.
- Enhances code modularity and testability.
- Simplifies the process of swapping implementations and improves overall structure.

### 🎨 Modern UI Development
- UI is built with **Jetpack Compose**, a modern toolkit for building native Android UI.
- **Compose Multiplatform** is used to share UI code across Android and iOS.
- Provides a **dynamic, responsive, and smooth user experience**.

### 🏛️ MVVM Architecture
- Follows the **MVVM (Model-View-ViewModel)** architectural pattern.
- Separates UI (View) from business logic (ViewModel), making the code **organized, testable, and maintainable**.
- ViewModels handle data fetching, processing, and exposure to the UI.

### 🔤 Custom Fonts
- Enhances the visual appeal with **custom fonts**.
- Provides a **polished and branded** user experience.

### 🌍 Currency Selection
- Users can select **source and target currencies** from a **comprehensive list**.
- Includes a **search function** for quick filtering by currency code or country name.
- Displays **country flags** for easy identification.

### 💱 Currency Conversion
- Users enter an amount, and the app **instantly calculates the equivalent value** in the target currency.

### 🔁 Currency Switching
- A **swap button** allows users to quickly reverse the **source and target currencies**.

### 🎯 Clean & Intuitive UI
- Designed with **Jetpack Compose** for a **smooth and responsive experience**.
- Ensures a **consistent design** across platforms, following platform-specific guidelines where necessary.

## 🛠️ Technologies Used

| Technology | Purpose |
|------------|---------|
| **Jetpack Compose Multiplatform (CMP)** | Cross-platform UI development |
| **Ktor** | Fetching real-time exchange rates |
| **MongoDB Realm** | Offline data storage & synchronization |
| **Koin** | Dependency injection |
| **MVVM Architecture** | Organizing business logic & UI separation |
| **Coroutines & Flow** | Asynchronous data handling |

🚀 **Currency Converter App** is a fast, reliable, and user-friendly solution for currency conversion on both **Android and iOS**. Whether you're a **traveler, business professional, or finance enthusiast**, this app ensures you always have accurate exchange rates at your fingertips! 🌍💱


This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
