plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
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

	implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

	implementation("com.squareup.okhttp3:okhttp:4.12.0")

	implementation("io.coil-kt.coil3:coil-compose:3.2.0")
	implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")

	implementation("androidx.media3:media3-exoplayer:1.7.1")
	implementation("androidx.media3:media3-ui:1.7.1")
	implementation("androidx.media3:media3-common:1.7.1")
}