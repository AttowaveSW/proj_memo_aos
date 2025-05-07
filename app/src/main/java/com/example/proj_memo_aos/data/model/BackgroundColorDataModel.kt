package com.example.proj_memo_aos.data.model

// 배경색을 위한 Data Model
enum class BackgroundColorDataModel(val displayName: String, val backColor: Int, val contentsColor: Int, val buttonColor: Int) {
    OFF_WHITE("화이트", 0xFFF0F0F0.toInt(), 0xFFFFFFFF.toInt(), 0xFF9E9E9E.toInt()),
    POWDER_BLUE("스카이 블루", 0xFFE3F2FD.toInt(), 0xFFEEF7FE.toInt(), 0xFF64B5F6.toInt()),
    SKY_BLUE("블루", 0xFFB3E5FC.toInt(), 0xFFD0F0FF.toInt(), 0xFF1976D2.toInt()),
    LAVENDER("라벤더", 0xFFF3E5F5.toInt(), 0xFFF8EEFA.toInt(), 0xFFD1A6D9.toInt()),
    PURPLE("퍼플", 0xFFE1D5FF.toInt(), 0xFFF7E9FF.toInt(), 0xFF9C6EE6.toInt()),
    PINK("핑크", 0xFFFFD6E7.toInt(), 0xFFF8E5F7.toInt(), 0xFFF48FB1.toInt()),
    MINT("소프트 민트", 0xFFE0F2F1.toInt(), 0xFFEAF9F8.toInt(), 0xFF80CBC4.toInt()),
    PALE_TURQUOISE("민트", 0xFFB2E8E1.toInt(), 0xFFDAF8F4.toInt(), 0xFF26A69A.toInt()),
    BEIGE("베이지", 0xFFF5F5DC.toInt(), 0xFFFDFDEB.toInt(), 0xFFD4A373.toInt()),
    CREAM("크림", 0xFFFFF2D5.toInt(), 0xFFFFF8E1.toInt(), 0xFFFFA500.toInt()),
    YELLOW("옐로우", 0xFFFFF3B0.toInt(), 0xFFFFF9D9.toInt(), 0xFFFFC107.toInt()),
    PEACH("피치", 0xFFFFD9B3.toInt(), 0xFFFDE9DB.toInt(), 0xFFFF8A65.toInt());

    companion object {
        // displayName과 매치되는 BackgroundColorDataModel를 반환해주는 함수
        fun fromDisplayName(displayName: String): BackgroundColorDataModel? {
            return entries.find { it.displayName == displayName }
        }

        fun getDefaultColor(): BackgroundColorDataModel{
            return CREAM
        }
    }
}