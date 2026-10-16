#!/usr/bin/env bash
# ==============================================================================
# SpendWealthy — Instant Multiplatform Runner (Android & iOS)
# ==============================================================================

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

# Resolve Android SDK
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "/Users/$USER/Library/Android/sdk" ]; then
        export ANDROID_HOME="/Users/$USER/Library/Android/sdk"
    fi
fi
if [ -n "$ANDROID_HOME" ]; then
    export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
fi

print_header() {
    echo -e "${PURPLE}${BOLD}"
    echo "============================================================"
    echo "  💎 SpendWealthy — Instant Runner (Android & iOS)"
    echo "============================================================"
    echo -e "${NC}"
}

usage() {
    print_header
    echo -e "${BOLD}Penggunaan:${NC}"
    echo -e "  ${GREEN}./run.sh ios${NC}       - Buka simulator iOS & jalankan aplikasi secara instan"
    echo -e "  ${GREEN}./run.sh android${NC}   - Buka emulator / device Android & jalankan aplikasi"
    echo -e "  ${GREEN}./run.sh both${NC}      - Jalankan Android dan iOS sekaligus berdampingan"
    echo -e "  ${GREEN}./run.sh fast-ios${NC}  - Jalankan build iOS terakhir di simulator (tanpa rebuild)"
    echo -e "  ${GREEN}./run.sh test${NC}      - Jalankan seluruh unit test (33 tests)"
    echo ""
    echo -e "${BOLD}Shortcut Makefile juga tersedia:${NC}"
    echo -e "  ${CYAN}make ios${NC}       |  ${CYAN}make android${NC}  |  ${CYAN}make both${NC}  |  ${CYAN}make test${NC}"
    echo ""
}

# ------------------------------------------------------------------------------
# iOS Runner
# ------------------------------------------------------------------------------
run_ios() {
    local FAST_MODE="$1"
    echo -e "${BLUE}📱 Menyiapkan iOS Simulator...${NC}"

    # 1. Check / Boot Simulator
    local BOOTED_DEVICE_ID
    BOOTED_DEVICE_ID=$(xcrun simctl list devices | grep -E "iPhone.*\(Booted\)" | head -n 1 | sed -E 's/.*\(([A-F0-9-]+)\).*/\1/' || true)

    if [ -z "$BOOTED_DEVICE_ID" ]; then
        echo -e "${YELLOW}Simulator belum aktif. Mencari iPhone simulator yang tersedia...${NC}"
        # Cari iPhone 17 Pro atau iPhone apapun yang tersedia
        BOOTED_DEVICE_ID=$(xcrun simctl list devices available | grep -E "iPhone 17 Pro \(" | head -n 1 | sed -E 's/.*\(([A-F0-9-]+)\).*/\1/' || true)
        
        if [ -z "$BOOTED_DEVICE_ID" ]; then
            BOOTED_DEVICE_ID=$(xcrun simctl list devices available | grep -E "iPhone" | head -n 1 | sed -E 's/.*\(([A-F0-9-]+)\).*/\1/' || true)
        fi

        if [ -z "$BOOTED_DEVICE_ID" ]; then
            echo -e "${RED}❌ Tidak ditemukan iOS Simulator. Silakan buat simulator di Xcode.${NC}"
            exit 1
        fi

        echo -e "${CYAN}🚀 Mem-boot simulator (UUID: $BOOTED_DEVICE_ID)...${NC}"
        xcrun simctl boot "$BOOTED_DEVICE_ID"
    else
        echo -e "${GREEN}✓ Simulator aktif ditemukan (UUID: $BOOTED_DEVICE_ID).${NC}"
    fi

    # 2. Buka jendela Simulator.app ke layar
    echo -e "${CYAN}🖥️  Membuka jendela Simulator.app...${NC}"
    open -a Simulator

    # 3. Build iOS app jika tidak fast mode atau belum ada app
    local DERIVED_APP
    DERIVED_APP=$(find "$HOME/Library/Developer/Xcode/DerivedData" -name "Spendwealthy.app" 2>/dev/null | head -n 1 || true)

    if [ "$FAST_MODE" != "true" ] || [ -z "$DERIVED_APP" ]; then
        echo -e "${BLUE}🔨 Mengompilasi framework & project iOS...${NC}"
        ./gradlew :composeApp:compileKotlinIosSimulatorArm64
        
        # Build via xcodebuild
        xcodebuild -project iosApp/iosApp.xcodeproj \
            -scheme iosApp \
            -configuration Debug \
            -destination "id=$BOOTED_DEVICE_ID" \
            -derivedDataPath iosApp/build \
            build

        DERIVED_APP=$(find iosApp/build -name "Spendwealthy.app" 2>/dev/null | head -n 1 || true)
        if [ -z "$DERIVED_APP" ]; then
            DERIVED_APP=$(find "$HOME/Library/Developer/Xcode/DerivedData" -name "Spendwealthy.app" 2>/dev/null | head -n 1 || true)
        fi
    fi

    if [ -n "$DERIVED_APP" ] && [ -d "$DERIVED_APP" ]; then
        echo -e "${GREEN}📦 Memasang bundle aplikasi: $DERIVED_APP${NC}"
        xcrun simctl install "$BOOTED_DEVICE_ID" "$DERIVED_APP"
    fi

    echo -e "${GREEN}🚀 Meluncurkan SpendWealthy di iOS Simulator...${NC}"
    xcrun simctl launch "$BOOTED_DEVICE_ID" com.spendwealthy.app.Spendwealthy
    open -a Simulator
    echo -e "${GREEN}✨ Sukses! Aplikasi iOS SpendWealthy aktif di simulator.${NC}"
}

# ------------------------------------------------------------------------------
# Android Runner
# ------------------------------------------------------------------------------
run_android() {
    echo -e "${BLUE}🤖 Menyiapkan Android Environment...${NC}"

    # 1. Cek apakah ada device / emulator terhubung
    local DEVICE_LIST
    DEVICE_LIST=$(adb devices | grep -v "List of devices" | grep "device$" | awk '{print $1}' || true)

    if [ -z "$DEVICE_LIST" ]; then
        echo -e "${YELLOW}Tidak ada Android device/emulator yang aktif.${NC}"
        local AVAILABLE_AVDS
        AVAILABLE_AVDS=$(emulator -list-avds 2>/dev/null || true)

        if [ -n "$AVAILABLE_AVDS" ]; then
            local FIRST_AVD
            FIRST_AVD=$(echo "$AVAILABLE_AVDS" | head -n 1)
            echo -e "${CYAN}Mencoba menyalakan AVD: $FIRST_AVD...${NC}"
            emulator -avd "$FIRST_AVD" -no-snapshot-load > /dev/null 2>&1 &
            echo -e "${YELLOW}Menunggu emulator boot (adb wait-for-device)...${NC}"
            
            # Timeout wait 30s
            local count=0
            while [ $count -lt 15 ]; do
                if adb devices | grep -q "device$"; then
                    break
                fi
                sleep 2
                count=$((count + 1))
            done
        fi
    fi

    DEVICE_LIST=$(adb devices | grep -v "List of devices" | grep "device$" | awk '{print $1}' || true)

    if [ -z "$DEVICE_LIST" ]; then
        echo -e "${YELLOW}⚠️  Perhatian: Tidak ada emulator/device Android aktif.${NC}"
        echo -e "   - Hubungkan HP Android fisik via USB (aktifkan USB Debugging), ATAU"
        echo -e "   - Buka Device Manager di Android Studio dan jalankan emulator."
        echo -e "${BLUE}Tetap mengompilasi APK debug...${NC}"
        ./gradlew :composeApp:assembleDebug
        echo -e "${GREEN}✓ APK debug selesai dibuat di: composeApp/build/outputs/apk/debug/composeApp-debug.apk${NC}"
        return 0
    fi

    echo -e "${GREEN}✓ Terhubung ke device: $DEVICE_LIST${NC}"
    echo -e "${BLUE}🔨 Memasang aplikasi ke Android device...${NC}"
    ./gradlew :composeApp:installDebug

    echo -e "${GREEN}🚀 Meluncurkan SpendWealthy di Android...${NC}"
    adb shell am start -n com.spendwealthy.app/.MainActivity
    echo -e "${GREEN}✨ Sukses! Aplikasi Android SpendWealthy aktif.${NC}"
}

# ------------------------------------------------------------------------------
# Dispatcher
# ------------------------------------------------------------------------------
case "$1" in
    ios|iphone)
        run_ios "false"
        ;;
    fast-ios)
        run_ios "true"
        ;;
    android)
        run_android
        ;;
    both|all)
        echo -e "${PURPLE}${BOLD}🚀 Menjalankan Android dan iOS sekaligus...${NC}"
        run_ios "true" &
        run_android &
        wait
        echo -e "${GREEN}✨ Selesai! Kedua platform telah dijalankan.${NC}"
        ;;
    test)
        echo -e "${BLUE}🧪 Menjalankan seluruh test suite...${NC}"
        ./gradlew testDebugUnitTest
        ;;
    *)
        usage
        ;;
esac
