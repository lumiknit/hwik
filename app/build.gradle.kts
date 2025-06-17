plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)

	kotlin("plugin.serialization") version "2.1.20"
}

android {
	namespace = "lumiknit.app.hwik"
	compileSdk = 35

	defaultConfig {
		applicationId = "lumiknit.app.hwik"
		minSdk = 34
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)

	implementation("com.google.guava:guava:31.0.1-android")

	implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.6.0")

	implementation("androidx.datastore:datastore-preferences:1.1.7")

	implementation("com.squareup.okhttp3:okhttp:4.12.0")

	implementation("io.coil-kt.coil3:coil-compose:3.2.0")
	implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")

	implementation("androidx.media3:media3-exoplayer:1.7.1")
	implementation("androidx.media3:media3-ui:1.7.1")
	implementation("androidx.media3:media3-common:1.7.1")

	implementation("androidx.javascriptengine:javascriptengine:1.0.0-rc01")
}