package sidly.wynnadhoc.utils.build_making;

public class SkillPoints {

    public int strength = 0;
    public int dexterity = 0;
    public int intelligence = 0;
    public int defence = 0;
    public int agility = 0;

    public SkillPoints() {
    }

    public SkillPoints(StatType type, int value) {
        switch (type) {
            case STRENGTH -> this.strength = value;
            case DEXTERITY -> this.dexterity = value;
            case INTELLIGENCE -> this.intelligence = value;
            case DEFENCE -> this.defence = value;
            case AGILITY -> this.agility = value;
        }
    }

    public SkillPoints(int strength, int dexterity, int intelligence, int defence, int agility) {
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.defence = defence;
        this.agility = agility;
    }

    public SkillPoints(SkillPoints sp) {
        this.strength = sp.strength;
        this.dexterity = sp.dexterity;
        this.intelligence = sp.intelligence;
        this.defence = sp.defence;
        this.agility = sp.agility;
    }

    public void add(SkillPoints sp) {
        this.strength += sp.strength;
        this.dexterity += sp.dexterity;
        this.intelligence += sp.intelligence;
        this.defence += sp.defence;
        this.agility += sp.agility;
    }

    public void subtract(SkillPoints sp) {
        this.strength -= sp.strength;
        this.dexterity -= sp.dexterity;
        this.intelligence -= sp.intelligence;
        this.defence -= sp.defence;
        this.agility -= sp.agility;
    }

    public SkillPoints intersect(SkillPoints other) {
        return new SkillPoints(
                Math.min(this.strength, other.strength),
                Math.min(this.dexterity, other.dexterity),
                Math.min(this.intelligence, other.intelligence),
                Math.min(this.defence, other.defence),
                Math.min(this.agility, other.agility)
        );
    }

    public static SkillPoints deficit(SkillPoints required, SkillPoints available) {
        // only positive deficit counts
        return new SkillPoints(
                Math.max(required.strength - available.strength, 0),
                Math.max(required.dexterity - available.dexterity, 0),
                Math.max(required.intelligence - available.intelligence, 0),
                Math.max(required.defence - available.defence, 0),
                Math.max(required.agility - available.agility, 0)
        );
    }

    public static SkillPoints max(SkillPoints minManual, SkillPoints d) {
        return new SkillPoints(
                Math.max(minManual.strength, d.strength),
                Math.max(minManual.dexterity, d.dexterity),
                Math.max(minManual.intelligence, d.intelligence),
                Math.max(minManual.defence, d.defence),
                Math.max(minManual.agility, d.agility)
        );
    }

    public SkillPoints maskZeroRequirements(SkillPoints requirements) {
        return new SkillPoints(
                requirements.strength == 0 ? 0 : this.strength,
                requirements.dexterity == 0 ? 0 : this.dexterity,
                requirements.intelligence == 0 ? 0 : this.intelligence,
                requirements.defence == 0 ? 0 : this.defence,
                requirements.agility == 0 ? 0 : this.agility
        );
    }

    public int total() {
        return strength + dexterity + intelligence + defence + agility;
    }

    public int totalPositive() {
        return Math.max(0, strength)
                + Math.max(0, dexterity)
                + Math.max(0, intelligence)
                + Math.max(0, defence)
                + Math.max(0, agility);
    }

    /**
     * Returns true if this skill point pool meets (>=) all requirements in req.
     */
    public boolean meets(SkillPoints req) {
        return this.strength >= req.strength &&
                this.dexterity >= req.dexterity &&
                this.intelligence >= req.intelligence &&
                this.defence >= req.defence &&
                this.agility >= req.agility;
    }

    /**
     * Raises this base SkillPoints just enough so that (current + base) meets req.
     * Does not lower any stat; only increases as needed.
     */
    public void raiseToMeet(SkillPoints req, SkillPoints current) {
        if (this.strength + current.strength < req.strength) {
            this.strength = req.strength - current.strength;
        }
        if (this.dexterity + current.dexterity < req.dexterity) {
            this.dexterity = req.dexterity - current.dexterity;
        }
        if (this.intelligence + current.intelligence < req.intelligence) {
            this.intelligence = req.intelligence - current.intelligence;
        }
        if (this.defence + current.defence < req.defence) {
            this.defence = req.defence - current.defence;
        }
        if (this.agility + current.agility < req.agility) {
            this.agility = req.agility - current.agility;
        }
    }

    /**
     * Returns true if this set of requirements is strictly "better" (lower or equal in all stats,
     * and lower in at least one) compared to other.
     */
    public boolean isBetterThan(SkillPoints other) {
        boolean strictlyBetter = false;

        if (this.strength > other.strength) return false;
        if (this.strength < other.strength) strictlyBetter = true;

        if (this.dexterity > other.dexterity) return false;
        if (this.dexterity < other.dexterity) strictlyBetter = true;

        if (this.intelligence > other.intelligence) return false;
        if (this.intelligence < other.intelligence) strictlyBetter = true;

        if (this.defence > other.defence) return false;
        if (this.defence < other.defence) strictlyBetter = true;

        if (this.agility > other.agility) return false;
        if (this.agility < other.agility) strictlyBetter = true;

        return strictlyBetter;
    }

    public SkillPoints copy() {
        return new SkillPoints(this);
    }

    public SkillPoints clampMin(int value) {
        return new SkillPoints(
                Math.max(this.strength, value),
                Math.max(this.dexterity, value),
                Math.max(this.intelligence, value),
                Math.max(this.defence, value),
                Math.max(this.agility, value)
        );
    }

    @Override
    public String toString() {
        return "SkillPoints: " +
                "str=" + strength +
                ", dex=" + dexterity +
                ", int=" + intelligence +
                ", def=" + defence +
                ", agi=" + agility;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SkillPoints sp) {
            if (this.strength != sp.strength) return false;
            if (this.dexterity != sp.dexterity) return false;
            if (this.intelligence != sp.intelligence) return false;
            if (this.defence != sp.defence) return false;
            return this.agility == sp.agility;
        } else return false;
    }


    // subtract b from a
    public static SkillPoints subtract(SkillPoints a, SkillPoints b) {
        return new SkillPoints(
                a.strength - b.strength,
                a.dexterity - b.dexterity,
                a.intelligence - b.intelligence,
                a.defence - b.defence,
                a.agility - b.agility
        );
    }

    public static SkillPoints add(SkillPoints a, SkillPoints b) {
        return new SkillPoints(
                a.strength + b.strength,
                a.dexterity + b.dexterity,
                a.intelligence + b.intelligence,
                a.defence + b.defence,
                a.agility + b.agility
        );
    }

    // percent dmgs
    public double getEarthPercentDamage() {
        return getEffectPercent(strength);
    }

    public double getThunderPercentDamage() {
        return getEffectPercent(dexterity);
    }

    public double getWaterPercentDamage() {
        return getEffectPercent(intelligence);
    }

    public double getFirePercentDamage() {
        return getDefenceScaling(defence);
    }

    public double getAirPercentDamage() {
        return getAgilityScaling(agility);
    }

    // normal effects
    public double getStrengthPercentDamage() {
        return getEffectPercent(strength);
    }

    public double getCritChance() {
        return getEffectPercent(dexterity);
    }

    public double getManaIncrease() {
        return getEffectPercent(intelligence);
    }

    public double getDamageReduction() {
        return getDefenceScaling(defence);
    }

    public double getDodgeChance() {
        return getAgilityScaling(agility);
    }


    public double getSpellCostReduction() {
        double effectPercent = getEffectPercent(intelligence);
        return (effectPercent / 80.8) * 50;
    }


    // strength earth% & dmg%, dex thunder% & crit%, int water% & mana increase
    private static double getEffectPercent(int value) {
        if (value < 0) value = 0;
        if (value > 150) value = 150;
        return switch (value) {
            case 1 -> 1.0;
            case 2 -> 2.0;
            case 3 -> 2.9;
            case 4 -> 3.9;
            case 5 -> 4.9;
            case 6 -> 5.8;
            case 7 -> 6.7;
            case 8 -> 7.7;
            case 9 -> 8.6;
            case 10 -> 9.5;
            case 11 -> 10.4;
            case 12 -> 11.3;
            case 13 -> 12.2;
            case 14 -> 13.1;
            case 15 -> 13.9;
            case 16 -> 14.8;
            case 17 -> 15.7;
            case 18 -> 16.5;
            case 19 -> 17.3;
            case 20 -> 18.2;
            case 21 -> 19.0;
            case 22 -> 19.8;
            case 23 -> 20.6;
            case 24 -> 21.4;
            case 25 -> 22.2;
            case 26 -> 23.0;
            case 27 -> 23.8;
            case 28 -> 24.6;
            case 29 -> 25.3;
            case 30 -> 26.1;
            case 31 -> 26.8;
            case 32 -> 27.6;
            case 33 -> 28.3;
            case 34 -> 29.0;
            case 35 -> 29.8;
            case 36 -> 30.5;
            case 37 -> 31.2;
            case 38 -> 31.9;
            case 39 -> 32.6;
            case 40 -> 33.3;
            case 41 -> 34.0;
            case 42 -> 34.6;
            case 43 -> 35.3;
            case 44 -> 36.0;
            case 45 -> 36.6;
            case 46 -> 37.3;
            case 47 -> 37.9;
            case 48 -> 38.6;
            case 49 -> 39.2;
            case 50 -> 39.9;
            case 51 -> 40.5;
            case 52 -> 41.1;
            case 53 -> 41.7;
            case 54 -> 42.3;
            case 55 -> 42.9;
            case 56 -> 43.5;
            case 57 -> 44.1;
            case 58 -> 44.7;
            case 59 -> 45.3;
            case 60 -> 45.8;
            case 61 -> 46.4;
            case 62 -> 47.0;
            case 63 -> 47.5;
            case 64 -> 48.1;
            case 65 -> 48.6;
            case 66 -> 49.2;
            case 67 -> 49.7;
            case 68 -> 50.3;
            case 69 -> 50.8;
            case 70 -> 51.3;
            case 71 -> 51.8;
            case 72 -> 52.3;
            case 73 -> 52.8;
            case 74 -> 53.4;
            case 75 -> 53.9;
            case 76 -> 54.3;
            case 77 -> 54.8;
            case 78 -> 55.3;
            case 79 -> 55.8;
            case 80 -> 56.3;
            case 81 -> 56.8;
            case 82 -> 57.2;
            case 83 -> 57.7;
            case 84 -> 58.1;
            case 85 -> 58.6;
            case 86 -> 59.1;
            case 87 -> 59.5;
            case 88 -> 59.9;
            case 89 -> 60.4;
            case 90 -> 60.8;
            case 91 -> 61.3;
            case 92 -> 61.7;
            case 93 -> 62.1;
            case 94 -> 62.5;
            case 95 -> 62.9;
            case 96 -> 63.3;
            case 97 -> 63.8;
            case 98 -> 64.2;
            case 99 -> 64.6;
            case 100 -> 65.0;
            case 101 -> 65.4;
            case 102 -> 65.7;
            case 103 -> 66.1;
            case 104 -> 66.5;
            case 105 -> 66.9;
            case 106 -> 67.3;
            case 107 -> 67.6;
            case 108 -> 68.0;
            case 109 -> 68.4;
            case 110 -> 68.7;
            case 111 -> 69.1;
            case 112 -> 69.4;
            case 113 -> 69.8;
            case 114 -> 70.1;
            case 115 -> 70.5;
            case 116 -> 70.8;
            case 117 -> 71.2;
            case 118 -> 71.5;
            case 119 -> 71.8;
            case 120 -> 72.2;
            case 121 -> 72.5;
            case 122 -> 72.8;
            case 123 -> 73.1;
            case 124 -> 73.5;
            case 125 -> 73.8;
            case 126 -> 74.1;
            case 127 -> 74.4;
            case 128 -> 74.7;
            case 129 -> 75.0;
            case 130 -> 75.3;
            case 131 -> 75.6;
            case 132 -> 75.9;
            case 133 -> 76.2;
            case 134 -> 76.5;
            case 135 -> 76.8;
            case 136 -> 77.1;
            case 137 -> 77.3;
            case 138 -> 77.6;
            case 139 -> 77.9;
            case 140 -> 78.2;
            case 141 -> 78.4;
            case 142 -> 78.7;
            case 143 -> 79.0;
            case 144 -> 79.2;
            case 145 -> 79.5;
            case 146 -> 79.8;
            case 147 -> 80.0;
            case 148 -> 80.3;
            case 149 -> 80.5;
            case 150 -> 80.8;
            default -> 0.0;
        };
    }

    private static double getDefenceScaling(int defence) {
        if (defence < 0) defence = 0;
        if (defence > 150) defence = 150;
        return switch (defence) {
            case 1 -> 0.9;
            case 2 -> 1.7;
            case 3 -> 2.5;
            case 4 -> 3.4;
            case 5 -> 4.2;
            case 6 -> 5.0;
            case 7 -> 5.8;
            case 8 -> 6.7;
            case 9 -> 7.4;
            case 10 -> 8.2;
            case 11 -> 9.0;
            case 12 -> 9.8;
            case 13 -> 10.6;
            case 14 -> 11.3;
            case 15 -> 12.0;
            case 16 -> 12.8;
            case 17 -> 13.6;
            case 18 -> 14.3;
            case 19 -> 15.0;
            case 20 -> 15.8;
            case 21 -> 16.5;
            case 22 -> 17.1;
            case 23 -> 17.8;
            case 24 -> 18.5;
            case 25 -> 19.2;
            case 26 -> 19.9;
            case 27 -> 20.6;
            case 28 -> 21.3;
            case 29 -> 21.9;
            case 30 -> 22.6;
            case 31 -> 23.2;
            case 32 -> 23.9;
            case 33 -> 24.5;
            case 34 -> 25.1;
            case 35 -> 25.8;
            case 36 -> 26.4;
            case 37 -> 27.0;
            case 38 -> 27.6;
            case 39 -> 28.2;
            case 40 -> 28.8;
            case 41 -> 29.4;
            case 42 -> 30.0;
            case 43 -> 30.6;
            case 44 -> 31.2;
            case 45 -> 31.7;
            case 46 -> 32.3;
            case 47 -> 32.8;
            case 48 -> 33.4;
            case 49 -> 33.9;
            case 50 -> 34.6;
            case 51 -> 35.1;
            case 52 -> 35.6;
            case 53 -> 36.1;
            case 54 -> 36.6;
            case 55 -> 37.2;
            case 56 -> 37.7;
            case 57 -> 38.2;
            case 58 -> 38.7;
            case 59 -> 39.2;
            case 60 -> 39.7;
            case 61 -> 40.2;
            case 62 -> 40.7;
            case 63 -> 41.1;
            case 64 -> 41.7;
            case 65 -> 42.1;
            case 66 -> 42.6;
            case 67 -> 43.0;
            case 68 -> 43.6;
            case 69 -> 44.0;
            case 70 -> 44.4;
            case 71 -> 44.9;
            case 72 -> 45.3;
            case 73 -> 45.7;
            case 74 -> 46.2;
            case 75 -> 46.7;
            case 76 -> 47.0;
            case 77 -> 47.5;
            case 78 -> 47.9;
            case 79 -> 48.3;
            case 80 -> 48.8;
            case 81 -> 49.2;
            case 82 -> 49.5;
            case 83 -> 50.0;
            case 84 -> 50.4;
            case 85 -> 50.8;
            case 86 -> 51.2;
            case 87 -> 51.6;
            case 88 -> 52.0;
            case 89 -> 52.4;
            case 90 -> 52.7;
            case 91 -> 53.1;
            case 92 -> 53.5;
            case 93 -> 53.8;
            case 94 -> 54.2;
            case 95 -> 54.6;
            case 96 -> 54.9;
            case 97 -> 55.3;
            case 98 -> 55.6;
            case 99 -> 56.0;
            case 100 -> 56.3;
            case 101 -> 56.7;
            case 102 -> 57.0;
            case 103 -> 57.3;
            case 104 -> 57.7;
            case 105 -> 58.0;
            case 106 -> 58.3;
            case 107 -> 58.6;
            case 108 -> 59.0;
            case 109 -> 59.3;
            case 110 -> 59.6;
            case 111 -> 59.9;
            case 112 -> 60.2;
            case 113 -> 60.5;
            case 114 -> 60.8;
            case 115 -> 61.1;
            case 116 -> 61.4;
            case 117 -> 61.7;
            case 118 -> 62.0;
            case 119 -> 62.3;
            case 120 -> 62.6;
            case 121 -> 62.9;
            case 122 -> 63.1;
            case 123 -> 63.4;
            case 124 -> 63.7;
            case 125 -> 64.0;
            case 126 -> 64.2;
            case 127 -> 64.5;
            case 128 -> 64.8;
            case 129 -> 65.0;
            case 130 -> 65.3;
            case 131 -> 65.6;
            case 132 -> 65.8;
            case 133 -> 66.1;
            case 134 -> 66.3;
            case 135 -> 66.6;
            case 136 -> 66.8;
            case 137 -> 67.1;
            case 138 -> 67.3;
            case 139 -> 67.5;
            case 140 -> 67.8;
            case 141 -> 68.0;
            case 142 -> 68.2;
            case 143 -> 68.5;
            case 144 -> 68.7;
            case 145 -> 68.9;
            case 146 -> 69.2;
            case 147 -> 69.4;
            case 148 -> 69.6;
            case 149 -> 69.8;
            case 150 -> 70.0;
            default -> 0.0; // If defence is out of range
        };
    }

    private static double getAgilityScaling(int agility) {
        if (agility < 0) agility = 0;
        if (agility > 150) agility = 150;
        return switch (agility) {
            case 1 -> 0.9;
            case 2 -> 1.9;
            case 3 -> 2.8;
            case 4 -> 3.7;
            case 5 -> 4.6;
            case 6 -> 5.5;
            case 7 -> 6.4;
            case 8 -> 7.3;
            case 9 -> 8.2;
            case 10 -> 9.0;
            case 11 -> 9.9;
            case 12 -> 10.8;
            case 13 -> 11.6;
            case 14 -> 12.4;
            case 15 -> 13.3;
            case 16 -> 14.1;
            case 17 -> 14.9;
            case 18 -> 15.7;
            case 19 -> 16.5;
            case 20 -> 17.3;
            case 21 -> 18.1;
            case 22 -> 18.8;
            case 23 -> 19.6;
            case 24 -> 20.4;
            case 25 -> 21.1;
            case 26 -> 21.9;
            case 27 -> 22.6;
            case 28 -> 23.4;
            case 29 -> 24.1;
            case 30 -> 24.8;
            case 31 -> 25.5;
            case 32 -> 26.2;
            case 33 -> 26.9;
            case 34 -> 27.6;
            case 35 -> 28.3;
            case 36 -> 29.0;
            case 37 -> 29.7;
            case 38 -> 30.3;
            case 39 -> 31.0;
            case 40 -> 31.7;
            case 41 -> 32.3;
            case 42 -> 32.9;
            case 43 -> 33.6;
            case 44 -> 34.2;
            case 45 -> 34.8;
            case 46 -> 35.5;
            case 47 -> 36.1;
            case 48 -> 36.7;
            case 49 -> 37.3;
            case 50 -> 37.9;
            case 51 -> 38.5;
            case 52 -> 39.1;
            case 53 -> 39.7;
            case 54 -> 40.2;
            case 55 -> 40.8;
            case 56 -> 41.4;
            case 57 -> 41.9;
            case 58 -> 42.5;
            case 59 -> 43.1;
            case 60 -> 43.6;
            case 61 -> 44.1;
            case 62 -> 44.7;
            case 63 -> 45.2;
            case 64 -> 45.7;
            case 65 -> 46.3;
            case 66 -> 46.8;
            case 67 -> 47.3;
            case 68 -> 47.8;
            case 69 -> 48.3;
            case 70 -> 48.8;
            case 71 -> 49.3;
            case 72 -> 49.8;
            case 73 -> 50.3;
            case 74 -> 50.7;
            case 75 -> 51.2;
            case 76 -> 51.7;
            case 77 -> 52.1;
            case 78 -> 52.6;
            case 79 -> 53.1;
            case 80 -> 53.5;
            case 81 -> 54.0;
            case 82 -> 54.4;
            case 83 -> 54.9;
            case 84 -> 55.3;
            case 85 -> 55.7;
            case 86 -> 56.2;
            case 87 -> 56.6;
            case 88 -> 57.0;
            case 89 -> 57.4;
            case 90 -> 57.8;
            case 91 -> 58.3;
            case 92 -> 58.7;
            case 93 -> 59.1;
            case 94 -> 59.5;
            case 95 -> 59.9;
            case 96 -> 60.2;
            case 97 -> 60.6;
            case 98 -> 61.0;
            case 99 -> 61.4;
            case 100 -> 61.8;
            case 101 -> 62.2;
            case 102 -> 62.5;
            case 103 -> 62.9;
            case 104 -> 63.3;
            case 105 -> 63.6;
            case 106 -> 64.0;
            case 107 -> 64.3;
            case 108 -> 64.7;
            case 109 -> 65.0;
            case 110 -> 65.4;
            case 111 -> 65.7;
            case 112 -> 66.0;
            case 113 -> 66.4;
            case 114 -> 66.7;
            case 115 -> 67.0;
            case 116 -> 67.4;
            case 117 -> 67.7;
            case 118 -> 68.0;
            case 119 -> 68.3;
            case 120 -> 68.6;
            case 121 -> 68.9;
            case 122 -> 69.3;
            case 123 -> 69.6;
            case 124 -> 69.9;
            case 125 -> 70.2;
            case 126 -> 70.5;
            case 127 -> 70.8;
            case 128 -> 71.0;
            case 129 -> 71.3;
            case 130 -> 71.6;
            case 131 -> 71.9;
            case 132 -> 72.2;
            case 133 -> 72.5;
            case 134 -> 72.7;
            case 135 -> 73.0;
            case 136 -> 73.3;
            case 137 -> 73.5;
            case 138 -> 73.8;
            case 139 -> 74.1;
            case 140 -> 74.3;
            case 141 -> 74.6;
            case 142 -> 74.9;
            case 143 -> 75.1;
            case 144 -> 75.4;
            case 145 -> 75.6;
            case 146 -> 75.9;
            case 147 -> 76.1;
            case 148 -> 76.3;
            case 149 -> 76.6;
            case 150 -> 76.8;
            default -> 0.0; // Out of range
        };
    }

    public int get(StatType stat) {
        return switch (stat) {
            case STRENGTH -> this.strength;
            case DEXTERITY -> this.dexterity;
            case INTELLIGENCE -> this.intelligence;
            case DEFENCE -> this.defence;
            case AGILITY -> this.agility;
        };
    }
}
