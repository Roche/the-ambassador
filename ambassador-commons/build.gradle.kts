plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val slf4jVersion: String by extra
val kotlinCoroutinesVersion: String by extra
val jacksonVersion: String by extra
val flexmarkVersion: String by extra

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api("com.vladsch.flexmark:flexmark:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-abbreviation:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-admonition:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-anchorlink:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-aside:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-attributes:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-autolink:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-definition:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-emoji:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-enumerated-reference:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-escaped-character:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-footnotes:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-gfm-strikethrough:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-gfm-tasklist:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-media-tags:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-ins:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-superscript:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-tables:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-toc:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-typographic:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-wikilink:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-yaml-front-matter:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-youtube-embedded:$flexmarkVersion")
    api("com.vladsch.flexmark:flexmark-util-ast:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-util-collection:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-util-data:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-util-format:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-util-misc:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-util-options:$flexmarkVersion")
    api("com.vladsch.flexmark:flexmark-util-visitor:$flexmarkVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
}

description = "ambassador-commons"
