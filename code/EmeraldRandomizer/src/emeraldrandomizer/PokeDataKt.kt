package emeraldrandomizer

class PokeDataKt(
        var stats: IntArray = intArrayOf(1,1,1,1,1,1),
        var types: IntArray = intArrayOf(0,0),
        var evs: IntArray = intArrayOf(0,0,0,0,0,0),
        var abilities: IntArray = intArrayOf(1,0),
        var heldItems: IntArray = intArrayOf(0,0),
        var statSwaps: IntArray = intArrayOf(0,1,2,3,4,5,6),
        var TMCompat: IntArray = intArrayOf(0,0,0,0,0,0,0,0),
        var tutorCompat: IntArray = intArrayOf(0,0,0,0)) {

    constructor(data: IntArray): this(){
        if (data.size < 28){
            error("Error parsing Pokemon Data: Expected 28 bytes, got " + data.size)
        }

        for (i in 0..6){
            this.stats[i] = data[i]
            this.evs[i] = data[10 + i / 4] shr i * 2 % 8 and 3
        }
        this.types[0] = data[6]
        this.types[1] = data[7]
        this.abilities[0] = data[22]
        this.abilities[1] = data[23]
        this.heldItems = intArrayOf(data[13] * 256 + data[12], data[15] * 256 + data[14])
    }

    fun setStats(hp: Int,
                 at: Int,
                 df: Int,
                 sa: Int,
                 sd: Int,
                 sp: Int): Unit {
        stats[0] = hp
        stats[1] = at
        stats[2] = df
        stats[3] = sa
        stats[4] = sd
        stats[5] = sp
    }

    fun setType(typeSlot: Int, typeToChangeTo: Int): Unit {
        if (!(typeSlot in 1..2)){
            print("Invalid type slot: " + typeSlot + " -- type change ignored.")
        }
        if ((!(typeToChangeTo in 0..17)) || typeToChangeTo == 9){
            print("Invalid type: " + typeToChangeTo + " -- Setting type to Normal.")
            types[typeSlot] = 0
        }
        else types[typeSlot] = typeToChangeTo
    }

    fun setTypes(firstType: Int, secondType: Int): Unit {
        setType(0, firstType)
        setType(1, secondType)
    }

    fun setEVs(evIn: IntArray): Unit {
        for (i in 0..6){
            if (!(evIn[i] in 0..3)){
                print("Bad EV Value: " + evIn[i] + " -- using 0 instead")
                evs[i] = 0
            } else evs[i] = evIn[i]
        }
    }

    fun setAbility(abilitySlot: Int, abilityToChangeTo: Int): Unit {
        if (!(abilitySlot in 1..2)){
            print("Invalid ability slot: " + abilitySlot+ " -- ability change ignored.")
        }
        if ((!(abilityToChangeTo in 0..77))){
            print("Invalid ability: " + abilityToChangeTo+ " -- Setting ability to Stench (01) instead.")
            abilities[abilitySlot] == 1
        } else if (abilitySlot == 0 && abilityToChangeTo == 0){
            print("First ability cannot be 0 -- Changing ability to Stench (01) instead")
        }
        else abilities[abilitySlot] == abilityToChangeTo
    }

    fun setAbilities(firstAbility: Int, secondAbility: Int): Unit {
        setAbility(0, firstAbility)
        setAbility(1, secondAbility)
    }


}