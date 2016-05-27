package com.feed_the_beast.ftbl.api;

import java.util.Comparator;

public class ForgePlayerComparators
{
    public static class ByName implements Comparator<ForgePlayer>
    {
        @Override
        public int compare(ForgePlayer o1, ForgePlayer o2)
        {
            return o1.getProfile().getName().compareToIgnoreCase(o2.getProfile().getName());
        }
    }

    public static class ByStatus implements Comparator<ForgePlayer>
    {
        public final Team team;

        public ByStatus(ForgePlayer p)
        {
            team = p.getTeam();
        }

        @Override
        public int compare(ForgePlayer p1, ForgePlayer p2)
        {
            int output = team.getStatusOf(p2).status - team.getStatusOf(p1).status;

            if(output == 0)
            {
                boolean o1 = p1.isOnline();
                boolean o2 = p2.isOnline();

                if(o1 && !o2)
                {
                    output = -1;
                }
                else if(!o1 && o2)
                {
                    output = 1;
                }
                else if((o1 && o2) || (!o1 && !o2))
                {
                    output = 0;
                }
                if(output == 0)
                {
                    output = p1.getProfile().getName().compareToIgnoreCase(p2.getProfile().getName());
                }
            }

            return output;
        }
    }
}