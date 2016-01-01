#! python

src = """
net/minecraft/world/WorldServer/tick ()V
net/minecraft/client/multiplayer/WorldClient/tick ()V
net/minecraft/client/multiplayer/WorldClient
net/minecraft/world/World/getWorldTime ()J
net/minecraft/world/World/worldInfo
net/minecraft/world/storage/WorldInfo/getWorldTime ()J
net/minecraft/world/storage/WorldInfo/setWorldTime (J)V
net/minecraft/world/WorldProvider/calculateCelestialAngle (JF)F
net/minecraft/block/Block/addCollisionBoxesToList (Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V
net/minecraft/entity/item/EntityBoat
net/minecraft/entity/Entity/moveEntity (DDD)V
net/minecraft/entity/Entity/isCollidedHorizontally
"""

src = set(src.strip().splitlines())
with open('/home/mati/.gradle/caches/minecraft/de/oceanlabs/mcp/mcp_snapshot/20151212/srgs/srg-mcp.srg') as f:
    for line in f:
        parts = line.strip().split(" ")
        if parts[0] == 'CL:':
            if parts[2] in src:
                src.remove(parts[2])
                print (".addClsSrg(\"%s\", \"%s\")" % (parts[2], parts[1]))
        elif parts[0] == 'FD:':
            if parts[2] in src:
                src.remove(parts[2])
                print (".addFldSrg(\"%s\", \"%s\")" % (parts[2], parts[1]))
        elif parts[0] == 'MD:':
            if parts[3] + ' ' + parts[4] in src:
                src.remove(parts[3] + ' ' + parts[4])
                print (".addMthdSrg(\"%s\", \"%s\", \"%s\")" % (parts[3], parts[1], parts[4]))

print("Missing mappings:")
for m in src:
    print("  * " + m)
