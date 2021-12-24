package com.roche.ambassador.advisor.badges

interface BadgeProvider {
    
    fun getBadgeAsMarkdown(badge: Badge): String
    
}