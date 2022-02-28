/* 
获得敌方各单位的坐标

判断人类玩家avatar的坐标，avatar向其移动最大距离
if(avatar攻击范围内有敌方avatar && avatar没有被嘲讽){
    avatar攻击敌方avatar
}

if(手牌中有可以使用的spell){
    if(spell == Staff of Y'kir){
        AI出牌()
    }
    if (spell == Entropic_Decay){
        if(场上有enemy ironcliff_guardian){
            AI出牌()
            直接解掉
        }else{
            留着
        }
    }
}

if(手牌中有可以放置的unit){
    if(unit == Serpenti && Serpenti能直接攻击到敌方avatar){
        if(敌方能够嘲讽到Serpenti){
            不放了
        }
        if(我方有嘲讽，能保护Serpenti第一轮不被解掉){
            放
        }
    }
    把unit放置到离avatar最近的地方
    if(unit 能攻击到敌方avatar){
        攻击敌方avatar
    }
}

/* unit的移动和攻击逻辑 */

/*
/**
Planar Scout placement logic

if(tiles around avatar is empty){
    place it on any of them
}
else if(tiles around avatar is not empty){
    place it on the 大的一圈
}

Pyromancer placement logic

if(右上角是空的){
    place it on 右上角
}else if(右下角是空的){
    place it on 左上角
}else{
    place it on any tile
}


 */

 



/*
AI出牌(){
    if(AI出牌检测()==true){
        unit.setPositionBYTile();
        unit.showUnit();
        return true;
    }else{
        return false
    }  
}
*/

/**
 获得敌方单位及其坐标

 get
  
 */

