package emeraldrandomizer

import java.util.*

class PokeDataKt(
        var stats: IntArray = intArrayOf(1,1,1,1,1,1),
        var types: IntArray = intArrayOf(0,0),
        var evs: IntArray = intArrayOf(0,0,0,0,0,0),
        var abilities: IntArray = intArrayOf(1,0),
        var heldItems: IntArray = intArrayOf(0,0),
        var statSwapArray: IntArray = intArrayOf(0,1,2,3,4,5,6),
        var TMCompat: IntArray = intArrayOf(0,0,0,0,0,0,0,0),
        var tutorCompat: IntArray = intArrayOf(0,0,0,0)) {

    private var attackHash: HashMap<Int, Int> = hashMapOf()
    private var availableMoves: MutableList<Int> = mutableListOf()


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
        if (typeSlot !in 1..2){
            print("Invalid type slot: $typeSlot -- type change ignored.")
            return
        }
        if ((typeToChangeTo !in 0..17) || typeToChangeTo == 9){
            print("Invalid type: $typeToChangeTo -- Setting type to Normal.")
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
            if (evIn[i] !in 0..3){
                print("Bad EV Value: " + evIn[i] + " -- using 0 instead")
                evs[i] = 0
            } else evs[i] = evIn[i]
        }
    }

    fun setAbility(abilitySlot: Int, abilityToChangeTo: Int): Unit {
        if (abilitySlot !in 1..2){
            print("Invalid ability slot: $abilitySlot -- ability change ignored.")
            return
        }
        if (abilityToChangeTo !in 0..77){
            print("Invalid ability: $abilityToChangeTo -- Setting ability to Stench (01) instead.")
            abilities[abilitySlot] = 1
        } else if (abilitySlot == 0 && abilityToChangeTo == 0){
            print("First ability cannot be 0 -- Changing ability to Stench (01) instead")
        }
        else abilities[abilitySlot] = abilityToChangeTo
    }

    fun setAbilities(firstAbility: Int, secondAbility: Int): Unit {
        setAbility(0, firstAbility)
        setAbility(1, secondAbility)
    }

    fun setHeldItem(itemSlot: Int, item: Int): Unit {
        if (itemSlot !in 0..1){
            print("Erroneous held item slot: $itemSlot -- item change ignored")
            return
        }
        if (item !in 0..346){
            print("Erroneous held item: $item -- setting it to item 0 instead")
            heldItems[itemSlot] = 0
        }
        else heldItems[itemSlot] = item
    }

    fun setHeldItems(firstItem: Int, secondItem: Int): Unit {
        setHeldItem(0, firstItem)
        setHeldItem(1, secondItem)
    }

    fun setStatSwaps(swaps: IntArray){
        if (swaps.size != 6){
            print("Error with stat swap array lengths. No stat swapping was done")
            return
        }

        if (swaps.filter({ (it > 5) or (it < 0) }).size != 6) {
            print("Error with stat swap points. No stat swapping was done")
            return
        }
        statSwapArray = swaps.copyOf()
    }

    fun setTMCompatability(compatabilityArray: IntArray){
        if (compatabilityArray.size < 8){
            print("TM Compatability array is too small")
            return
        }
        TMCompat = compatabilityArray.copyOf()
    }

    fun setTutorCompatability(compatabilityArray: IntArray){
        if (compatabilityArray.size < 4){
            print("Tutor Compatabilty array is too small")
            return
        }

        tutorCompat = compatabilityArray.copyOf()
    }

    fun getStat(slot: Int) = if (slot in 0..5) stats[slot] else 1

    fun getType(slot: Int) = if (slot in 0..1) stats[slot] else 0

    fun getEV(slot: Int) = if (slot in 0..5) evs[slot] else 0

    fun getAbility(slot: Int) = if (slot in 0..1) abilities[slot] else 0

    fun getItems() = heldItems

    fun getItem(slot: Int) = if (slot in 0..1) heldItems[slot] else 0

    fun getTMCompatability() = TMCompat

    fun getTutorCompatability() = tutorCompat

    fun swapStats(){
        if ((stats.size != 6) or (statSwapArray.size != 6) or (evs.size != 6)){
            print("Error with stat swap array lengths. No stats swapped")
            return
        }
        val statBackup = stats.copyOf()
        val evsBackup = evs.copyOf()
        for (i in 0..5){
            stats[i] = statBackup[statSwapArray[i]]
            evs[i] = evsBackup[statSwapArray[i]]
        }
    }

    fun swapStats(swaps: IntArray){
        setStatSwaps(swaps)
        swapStats()
    }

    fun randomizeStats(){
        val statBackup = stats.copyOf()
        val evsBackup = evs.copyOf()
        var slots = mutableListOf<Int>(0,1,2,3,4,5)
        val rand = Random()

        var randChox: Int
        for (i in 0..5){
            randChox = slots.removeAt(rand.nextInt(slots.size))
            stats[i] = statBackup[randChox]
            evs[i] = evsBackup[randChox]
            statSwapArray[i] = randChox
        }
    }

    fun randomizeAbilities(){
        val rand = Random()
        val alAbil: MutableList<Int> = abilities.toMutableList()
        abilities[0] = alAbil.removeAt(rand.nextInt(alAbil.size))
        if (rand.nextInt(100) < 50){
            abilities[1] = alAbil.removeAt(rand.nextInt(alAbil.size))
        }
        else {
            abilities[1] = abilities[0]
        }
    }

    fun getTypeList(): MutableList<Int>{
        val typeRange = 0..17
        val returnList: MutableList<Int> = mutableListOf<Int>()
        returnList.addAll(typeRange.asIterable())
        returnList.removeAt(9)
        return returnList
    }

    fun randomizeTypes() {
        val rand = Random()
        val alTypes: MutableList<Int> = getTypeList()
        val baseType = alTypes.removeAt(rand.nextInt(alTypes.size))
        types[0] = baseType
        if (rand.nextInt(100) < 47) {
            types[1] = alTypes.removeAt(rand.nextInt(alTypes.size))
        } else {
            types[1] = baseType
        }
    }

    fun rerandomizeTypes(){
        if (types.size != 2){
            types = intArrayOf(0,0)
            println(" !! Types array recast to allow 2 slots")
        }

        if (types[0] == types[1]){
            val rand = Random()
            val alTypes: MutableList<Int> = getTypeList()

            if (rand.nextInt(100) <= 21){
                types[1] = alTypes[rand.nextInt(alTypes.size)]
            }
        }
    }

    fun derandomizeTypes(){

    }
}