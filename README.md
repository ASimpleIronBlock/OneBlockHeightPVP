# OneBlockHeightPVP

模仿https://www.bilibili.com/video/BV1Fg411L7ji 玩法的插件

### 使用方法
这是一个服务器端的插件 测试环境为paper 版本为1.16.5
下载插件后放入plugins插件即可使用

### 游戏规则

游戏开始后,玩家会被随机传送到一片只有一格高的区域,并趴下,刚开始每个玩家都只会被给予一个木镐,玩家们需要在这片区域发育并击败对手

### 游戏机制

主世界,地狱,末地的资源依次少到多(可以通过配置文件修改),玩家可以通过趴在地图中间的附魔台五秒传送到下一个维度

维度的危险度会随着维度的资源数量而增加,主世界基本没什么危险,地狱有流速极快的岩浆,末地的所有玩家会发光,受到的伤害会增加,并且在末地可以使用末影水晶

地图中的木桶是类似奖励箱的机制,右键木桶或者破坏木桶可以获取资源(资源可以通过配置文件修改),地图中的树叶被破坏时一定会掉落苹果,砂砾被破坏时一定会掉落燧石,地狱疣块被破坏时一定会掉落地狱疣

### 指令

```
    /startGame <width> <height> <crossDimension>
```

开始游戏 地图的宽为width,高为height,如果crossDimension为true,则会跨维度随机传送玩家

``` 
    /autoStart <true/false>
```

启用或禁止自动开始游戏

### 配置文件

配置文件可以在\plugins\OneBlockPvPPlugin中找到  
#### 每个配置的作用: 

+ OverWorldGenerationConfigFile: 表示主世界方块生成概率配置相对于插件目录(就是上面那个)的位置
+ NetherGenerationConfigFile: 表示下界方块生成概率配置相对于插件目录(就是上面那个)的位置
+ TheEndGenerationConfigFile: 表示末地方块生成概率配置相对于插件目录(就是上面那个)的位置
+ AutoStartGamePlayers: 到达此玩家数后,游戏才会自动开始
+ DeathModeTimer: 死斗模式倒计时的时间(tick)
+ DeathModeMapWidth: 死斗模式地图的长
+ DeathModeMapHeight: 死斗模式地图的宽
+ RejoinTimer: 允许断线重连的时间(tick)
+ GameStartTime: 自动开始游戏的倒计时(tick)
+ ScoreboardChangeTime: 计分板项目的轮换时间(tick)
+ PreventLivingPlayerReceivingDeadPlayerMessage: 如果设为true,死了的玩家说话活着的玩家是看不见的
+ BarrelDropsConfigFile: 表示木桶物品掉落的概率配置相对于插件目录(就是上面那个)的位置
+ TheEndGlobalDamageMultiplier: 末地全局伤害乘数
+ TheEndEnvironmentalDamageMultiplier: 末地环境伤害乘数
+ TheEndPlayerDamageMultiplier: 末地玩家伤害乘数
+ SpawnPoint: 出生点的坐标

#### 地图的方块生成概率配置的写法:

```
    方块名 = 权重
```

例:

```
OAK_LOG = 40.0
BLACKSTONE = 100.0
NETHERRACK = 100.0
COAL_ORE = 30.0
IRON_ORE = 50.0
GOLD_ORE =30.0
DIAMOND_ORE = 25.0
LAPIS_ORE = 1.0
LAVA = 20.0
BARREL = 5.0
ANCIENT_DEBRIS = 10.0
GLOWSTONE = 25.0
CRYING_OBSIDIAN = 20.0
NETHER_WART_BLOCK = 3.0
HAY_BLOCK = 3.0
GRAVEL = 10.0
```
注意: 方块名必须是Material类的一个成员的名字  
#### 木桶掉落物的配置文件写法
```
    物品名,物品数量 = 权重
```
例:
```
BOW,1 = 5.0
NETHERITE_BLOCK,1 = 0.1
TRIDENT,1 = 5.0
END_CRYSTAL,1 = 5.0
TOTEM_OF_UNDYING,1 = 5.0
CROSSBOW,1 = 5.0
DIAMOND,1 = 10.0
GOLD_INGOT,2 = 10.0
IRON_INGOT,3 = 10.0
BLAZE_ROD,3 = 10.0
SUGAR_CANE,3 = 10.0
FEATHER,16 = 10.0
GUNPOWDER,16 = 10.0
ENCHANTED_BOOK,1 = 20.0
```


#### 效果图





![2021-10-07_19 10 07](https://user-images.githubusercontent.com/62505063/136378514-a09ea2b4-8018-438d-a41d-8785f0b2ea76.png)



