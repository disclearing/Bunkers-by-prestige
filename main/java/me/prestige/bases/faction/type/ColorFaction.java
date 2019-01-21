package me.prestige.bases.faction.type;

import com.customhcf.util.PersistableLocation;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColorFaction extends PlayerFaction implements ConfigurationSerializable {


    public ColorFaction(String name) {
        super(name);
    }

    public ColorFaction(final Map map) {
       super(map);
    }




    public static class GreenFaction extends ColorFaction implements ConfigurationSerializable{

        private boolean hasVillager;

        public GreenFaction() {
            super("Green");
            members.clear();
            deathsUntilRaidable = 5;
            hasVillager = false;
        }


        private PersistableLocation buyVillager;
        private PersistableLocation sellVillager;
        private PersistableLocation enchantVillager;
        private PersistableLocation blockVillager;

        public GreenFaction(final Map map) {
            super(map);
            buyVillager = (PersistableLocation) map.get("buyVillager");
            sellVillager = (PersistableLocation) map.get("sellVillager");
            enchantVillager = (PersistableLocation) map.get("enchantVillager");
            blockVillager = (PersistableLocation) map.get("blockVillager");

        }

        public Map serialize() {
            Map map = super.serialize();
            map.put("buyVillager", buyVillager);
            map.put("sellVillager", sellVillager);
            map.put("enchantVillager", enchantVillager);
            map.put("blockVillager", blockVillager);
            return map;
        }

        public void setBuyVillager(PersistableLocation buyVillager) {
            this.buyVillager = buyVillager;
        }

        public void setSellVillager(PersistableLocation sellVillager) {
            this.sellVillager = sellVillager;
        }

        public void setEnchantVillager(PersistableLocation enchantVillager) {
            this.enchantVillager = enchantVillager;
        }

        public void setBlockVillager(PersistableLocation blockVillager) {
            this.blockVillager = blockVillager;
        }

        public Location getBuyVillager() {
            return buyVillager.getLocation();
        }

        public Location getSellVillager() {
            return sellVillager.getLocation();
        }

        public Location getEnchantVillager() {
            return enchantVillager.getLocation();
        }

        public Location getBlockVillager() {
            return blockVillager.getLocation();
        }


        @Override
        public String getDisplayName(CommandSender sender) {
            return ChatColor.GREEN + "Green";
        }

        @Override
        public String getDisplayName(Faction other) {
            return ChatColor.GREEN + "Green";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.GREEN;
        }

        @Getter
        public boolean isHasVillager() {
            return hasVillager;
        }
        @Setter
        public void setHasVillager(boolean hasVillager) {
            this.hasVillager = hasVillager;
        }
    }


    public static class YellowFaction extends ColorFaction implements ConfigurationSerializable {

        private boolean hasVillager;


        public YellowFaction() {
            super("Yellow");
            members.clear();
            deathsUntilRaidable = 5;
        }

        private PersistableLocation buyVillager;
        private PersistableLocation sellVillager;
        private PersistableLocation enchantVillager;
        private PersistableLocation blockVillager;

        public YellowFaction(final Map map) {
            super(map);
            buyVillager = (PersistableLocation) map.get("buyVillager");
            sellVillager = (PersistableLocation) map.get("sellVillager");
            enchantVillager = (PersistableLocation) map.get("enchantVillager");
            blockVillager = (PersistableLocation) map.get("blockVillager");

        }

        public Map serialize() {
            Map map = super.serialize();
            map.put("buyVillager", buyVillager);
            map.put("sellVillager", sellVillager);
            map.put("enchantVillager", enchantVillager);
            map.put("blockVillager", blockVillager);
            return map;
        }

        public void setBuyVillager(PersistableLocation buyVillager) {
            this.buyVillager = buyVillager;
        }

        public void setSellVillager(PersistableLocation sellVillager) {
            this.sellVillager = sellVillager;
        }

        public void setEnchantVillager(PersistableLocation enchantVillager) {
            this.enchantVillager = enchantVillager;
        }

        public void setBlockVillager(PersistableLocation blockVillager) {
            this.blockVillager = blockVillager;
        }

        public Location getBuyVillager() {
            return buyVillager.getLocation();
        }

        public Location getSellVillager() {
            return sellVillager.getLocation();
        }

        public Location getEnchantVillager() {
            return enchantVillager.getLocation();
        }

        public Location getBlockVillager() {
            return blockVillager.getLocation();
        }
        @Override
        public String getDisplayName(CommandSender sender) {
            return ChatColor.YELLOW + "Yellow";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.YELLOW;
        }

        @Override
        public String getDisplayName(Faction other) {
            return ChatColor.YELLOW + "Yellow";
        }

        @Getter
        public boolean isHasVillager() {
            return hasVillager;
        }
        @Setter
        public void setHasVillager(boolean hasVillager) {
            this.hasVillager = hasVillager;
        }

    }
        public static class BlueFaction extends ColorFaction implements ConfigurationSerializable{

            private boolean hasVillager;


            public BlueFaction() {
            super("Blue");
            members.clear();
            deathsUntilRaidable = 5;
        }

            private PersistableLocation buyVillager;
            private PersistableLocation sellVillager;
            private PersistableLocation enchantVillager;
            private PersistableLocation blockVillager;

            public BlueFaction(final Map map) {
                super(map);
                buyVillager = (PersistableLocation) map.get("buyVillager");
                sellVillager = (PersistableLocation) map.get("sellVillager");
                enchantVillager = (PersistableLocation) map.get("enchantVillager");
                blockVillager = (PersistableLocation) map.get("blockVillager");

            }

            public Map serialize() {
                Map map = super.serialize();
                map.put("buyVillager", buyVillager);
                map.put("sellVillager", sellVillager);
                map.put("enchantVillager", enchantVillager);
                map.put("blockVillager", blockVillager);
                return map;
            }

            @Override
            public ChatColor getColor() {
                return ChatColor.BLUE;
            }

            public void setBuyVillager(PersistableLocation buyVillager) {
                this.buyVillager = buyVillager;
            }

            public void setSellVillager(PersistableLocation sellVillager) {
                this.sellVillager = sellVillager;
            }

            public void setEnchantVillager(PersistableLocation enchantVillager) {
                this.enchantVillager = enchantVillager;
            }

            public void setBlockVillager(PersistableLocation blockVillager) {
                this.blockVillager = blockVillager;
            }

            public Location getBuyVillager() {
                return buyVillager.getLocation();
            }

            public Location getSellVillager() {
                return sellVillager.getLocation();
            }

            public Location getEnchantVillager() {
                return enchantVillager.getLocation();
            }

            public Location getBlockVillager() {
                return blockVillager.getLocation();
            }

            @Override
        public String getDisplayName(CommandSender sender) {
            return ChatColor.BLUE + "Blue";
        }

        @Override
        public String getDisplayName(Faction other) {
            return ChatColor.BLUE + "Blue";
        }

            @Getter
            public boolean isHasVillager() {
                return hasVillager;
            }
            @Setter
            public void setHasVillager(boolean hasVillager) {
                this.hasVillager = hasVillager;
            }
    }
    public static class RedFaction extends ColorFaction implements ConfigurationSerializable{

        private boolean hasVillager;


        public RedFaction() {
            super("Red");
            members.clear();
            deathsUntilRaidable = 5;
        }

        private PersistableLocation buyVillager;
        private PersistableLocation sellVillager;
        private PersistableLocation enchantVillager;
        private PersistableLocation blockVillager;

        public RedFaction(final Map map) {
            super(map);
            buyVillager = (PersistableLocation) map.get("buyVillager");
            sellVillager = (PersistableLocation) map.get("sellVillager");
            enchantVillager = (PersistableLocation) map.get("enchantVillager");
            blockVillager = (PersistableLocation) map.get("blockVillager");

        }

        public Map serialize() {
            Map map = super.serialize();
            map.put("buyVillager", buyVillager);
            map.put("sellVillager", sellVillager);
            map.put("enchantVillager", enchantVillager);
            map.put("blockVillager", blockVillager);
            return map;
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.RED;
        }

        public void setBuyVillager(PersistableLocation buyVillager) {
            this.buyVillager = buyVillager;
        }

        public void setSellVillager(PersistableLocation sellVillager) {
            this.sellVillager = sellVillager;
        }

        public void setEnchantVillager(PersistableLocation enchantVillager) {
            this.enchantVillager = enchantVillager;
        }

        public void setBlockVillager(PersistableLocation blockVillager) {
            this.blockVillager = blockVillager;
        }

        public Location getBuyVillager() {
            return buyVillager.getLocation();
        }

        public Location getSellVillager() {
            return sellVillager.getLocation();
        }

        public Location getEnchantVillager() {
            return enchantVillager.getLocation();
        }

        public Location getBlockVillager() {
            return blockVillager.getLocation();
        }

        @Override
        public String getDisplayName(CommandSender sender) {
            return ChatColor.RED + "Red";
        }

        @Override
        public String getDisplayName(Faction other) {
            return ChatColor.RED + "Red";
        }

        @Getter
        public boolean isHasVillager() {
            return hasVillager;
        }
        @Setter
        public void setHasVillager(boolean hasVillager) {
            this.hasVillager = hasVillager;
        }
    }

    public ChatColor getColor(){
        return null;
    }

    public void setBuyVillager(PersistableLocation buyVillager) {
    }

    public void setSellVillager(PersistableLocation sellVillager) {
    }

    public void setEnchantVillager(PersistableLocation enchantVillager) {
    }

    public void setBlockVillager(PersistableLocation blockVillager) {
    }

    public Location getBuyVillager() {
        return null;
    }

    public Location getSellVillager() {
        return null;
    }

    public Location getEnchantVillager() {
        return null;
    }

    public Location getBlockVillager() {
        return null;
    }

    public boolean isHasVillager() {
        return true;
    }
    public void setHasVillager(boolean hasVillager) {

    }







}
