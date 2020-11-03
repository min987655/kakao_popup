# Macci Web-App

### 개발환경
* Android Studio @4.0.1
* minSDK 19
* targetSDK 30
* Android SDK 2.0.2
***

### web 도메인
* m.macci.co.kr
***

### manifest.xml 설정
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CALL_PHONE" />
<application
        ...
        android:networkSecurityConfig="@xml/network_security_config">
```
***

### 라이브러리
* build.gradle(app)
```gradle
dependencies {
    ...
    implementation 'gun0912.ted:tedpermission:2.0.0'
    implementation 'com.google.android.material:material:1.2.0-alpha05'
```
* build.gradle(Project)
```gradle
allprojects {
    repositories {
        ...
        maven {url 'https://jitpack.io'}
    }
}
```
***

### KAKAO Legacy
1. build.gradle(app)
```gradle
dependencies {
    ...
    implementation group: 'com.kakao.sdk', name: 'kakaonavi', version: '1.30.2'
```
2. build.gradle(Project)
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://devrepo.kakao.com/nexus/content/groups/public/' }
```
3. gradle.properties
```properties
KAKAO_SDK_GROUP=com.kakao.sdk
KAKAO_SDK_VERSION=1.27.0
```
4. manifests
```xml
<application>
    ...
    <!-- kakao sdk legacy 설정 -->
    <meta-data
        android:name="com.kakao.sdk.AppKey"
        android:value="@string/kakao_app_key" />

    <!-- kakao 웹뷰 및 위치 서비스 사용하지 않는 경우 설정 -->
    <meta-data
        android:name="com.kakao.sdk.kakaonavi.useWebView"
        android:value="false" />
    ...
</application>
```
5. values/strings.xml
```xml
<resources>
    ...
    <string name="kakao_app_key">{카카오앱키}</string>
</resources>
```