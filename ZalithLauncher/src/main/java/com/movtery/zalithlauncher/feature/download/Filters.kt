package com.movtery.zalithlauncher.feature.download

import com.movtery.zalithlauncher.feature.download.enums.Category
import com.movtery.zalithlauncher.feature.download.enums.ModLoader
import com.movtery.zalithlauncher.feature.download.enums.Sort

/**
 * 用于平台进行搜索时，提供筛选信息
 */
class Filters {
    var name: String = ""
    var mcVersion: String? = null
    var modloader: ModLoader? = null
    var sort: Sort = Sort.RELEVANT
    var category: Category = Category.ALL

    fun copy(name: String = this.name, mcVersion: String? = this.mcVersion, modloader: ModLoader? = this.modloader, sort: Sort = this.sort, category: Category = this.category): Filters {
        val f = Filters()
        f.name = name
        f.mcVersion = mcVersion
        f.modloader = modloader
        f.sort = sort
        f.category = category
        return f
    }

    override fun toString(): String {
        return "Filters(name='$name', mcVersion=$mcVersion, modloader=$modloader, sort=$sort, category=$category)"
    }
}