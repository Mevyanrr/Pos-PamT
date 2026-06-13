package com.example.pos_pamt.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pos_pamt.R

val Figtree = FontFamily(
    Font(R.font.figtree_light,           FontWeight.Light),
    Font(R.font.figtree_lightitalic,     FontWeight.Light,     FontStyle.Italic),
    Font(R.font.figtree_regular,         FontWeight.Normal),
    Font(R.font.figtree_italic,          FontWeight.Normal,    FontStyle.Italic),
    Font(R.font.figtree_medium,          FontWeight.Medium),
    Font(R.font.figtree_mediumitalic,    FontWeight.Medium,    FontStyle.Italic),
    Font(R.font.figtree_semibold,        FontWeight.SemiBold),
    Font(R.font.figtree_semibolditalic,  FontWeight.SemiBold,  FontStyle.Italic),
    Font(R.font.figtree_bold,            FontWeight.Bold),
    Font(R.font.figtree_bolditalic,      FontWeight.Bold,      FontStyle.Italic),
    Font(R.font.figtree_extrabold,       FontWeight.ExtraBold),
    Font(R.font.figtree_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.figtree_black,           FontWeight.Black),
    Font(R.font.figtree_blackitalic,     FontWeight.Black,     FontStyle.Italic),
)

val PlusJakartaSans = FontFamily(
    Font(R.font.plusjakartasans_extralight,      FontWeight.ExtraLight),
    Font(R.font.plusjakartasans_extralightitalic,FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.plusjakartasans_light,           FontWeight.Light),
    Font(R.font.plusjakartasans_lightitalic,     FontWeight.Light,      FontStyle.Italic),
    Font(R.font.plusjakartasans_regular,         FontWeight.Normal),
    Font(R.font.plusjakartasans_italic,          FontWeight.Normal,     FontStyle.Italic),
    Font(R.font.plusjakartasans_medium,          FontWeight.Medium),
    Font(R.font.plusjakartasans_mediumitalic,    FontWeight.Medium,     FontStyle.Italic),
    Font(R.font.plusjakartasans_semibold,        FontWeight.SemiBold),
    Font(R.font.plusjakartasans_semibolditalic,  FontWeight.SemiBold,   FontStyle.Italic),
    Font(R.font.plusjakartasans_bold,            FontWeight.Bold),
    Font(R.font.plusjakartasans_bolditalic,      FontWeight.Bold,       FontStyle.Italic),
    Font(R.font.plusjakartasans_extrabold,       FontWeight.ExtraBold),
    Font(R.font.plusjakartasans_extrabolditalic, FontWeight.ExtraBold,  FontStyle.Italic),
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily  = PlusJakartaSans,
        fontWeight  = FontWeight.ExtraBold,
        fontSize    = 30.sp,
        lineHeight  = 36.sp,
        letterSpacing = (-1).sp
    ),
    headlineMedium = TextStyle(
        fontFamily  = PlusJakartaSans,
        fontWeight  = FontWeight.Bold,
        fontSize    = 19.sp,
        lineHeight  = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily  = PlusJakartaSans,
        fontWeight  = FontWeight.Bold,
        fontSize    = 21.sp,
        lineHeight  = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily  = PlusJakartaSans,
        fontWeight  = FontWeight.Bold,
        fontSize    = 13.sp,
        lineHeight  = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily    = Figtree,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp,
        lineHeight = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily    = PlusJakartaSans,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = PlusJakartaSans,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 10.sp,
        lineHeight    = 14.sp,
        letterSpacing = 0.6.sp
    )
)
