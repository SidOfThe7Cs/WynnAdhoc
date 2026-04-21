package sidly.wynnadhoc.wapi.item;

import sidly.wynnadhoc.utils.build_making.SkillPoints;

public record ItemOnlyIDs(int strengthRequirement, int dexterityRequirement, int intelligenceRequirement,
                          int defenceRequirement, int agilityRequirement, int durabilityModifier) {
    public SkillPoints getAsSp() {
        return new SkillPoints(
                this.strengthRequirement,
                this.dexterityRequirement,
                this.intelligenceRequirement,
                this.defenceRequirement,
                this.agilityRequirement
        );
    }
}
