package ca.bcit.net;


public class ModulationInIfStatement {
    public int[] modulationDistance(String modulation) {
        if(modulation.equals("QPSK")) {
            return new int[]{3429, 3010, 2765, 2591, 2456, 2346, 2253, 2172, 2101, 2037, 1980, 1927,
                    1879, 1834, 1792, 1753, 1717, 1682, 1649, 1618, 1589, 1561, 1534, 1508, 1484,
                    1460, 1437, 1415, 1394, 1373, 1354, 1334, 1316, 1298, 1280, 1263, 1247, 1231,
                    1215, 1200};
        }else if (modulation.equals("QAM16")) {
            return new int[]{2120, 1868, 1721, 1616, 1535, 1469, 1413, 1364, 1322, 1283, 1249, 1217,
                    1188, 1161, 1136, 1112, 1090, 1070, 1050, 1031, 1014, 997, 981, 965, 950, 936,
                    922, 909, 896, 884, 872, 861, 849, 839, 828, 818, 808, 798, 789, 779};
        }else if (modulation.equals("QAM32")) {
            return new int[]{1466, 1297, 1199, 1129, 1075, 1030, 993, 960, 932, 906, 883, 862, 842,
                    824, 808, 792, 777, 763, 750, 738, 726, 715, 704, 694, 684, 674, 665, 656, 648,
                    639, 631, 624, 616, 609, 602, 595, 588, 582, 576, 569};
        }else if (modulation.equals("BPSK")) {
            return new int[]{4083, 3581, 3287, 3078, 2917, 2784, 2673, 2576, 2491, 2414, 2345, 2282,
                    2224, 2170, 2120, 2074, 2030, 1988, 1949, 1912, 1877, 1843, 1811, 1780, 1750,
                    1722, 1694, 1668, 1643, 1618, 1594, 1571, 1549, 1527, 1506, 1486, 1466, 1447,
                    1428, 1410};
        }else if (modulation.equals("QAM8")) {
            return new int[]{2774, 2439, 2243, 2104, 1996, 1907, 1833, 1768, 1711, 1660, 1614, 1572,
                    1533, 1497, 1464, 1433, 1403, 1376, 1350, 1325, 1301, 1279, 1257, 1237, 1217,
                    1198, 1180, 1162, 1145, 1129, 1113, 1097, 1083, 1068, 1054, 1040, 1027, 1014,
                    1002, 989};
        }else {
            return new int[]{811, 726, 677, 641, 614, 592, 573, 557, 542, 529, 518, 507, 497, 488,
                    480, 472, 464, 457, 451, 444, 438, 433, 427, 422, 417, 412, 408, 403, 399, 395,
                    391, 387, 383, 379, 376, 372, 369, 366, 362, 359};
        }
    }

    public int[] slicesConsumption(String modulation) {
        if(modulation.equals("QPSK")) {
            return new int[]{4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10,
                    12, 12, 12, 12, 12, 14, 14, 14, 14, 14, 16, 16, 16, 16, 16, 18, 18, 18, 18, 18};
        }else if (modulation.equals("QAM16")) {
            return new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8,
                    8, 8, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        }else if (modulation.equals("QAM32")) {
            return new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
                    6, 6, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 10, 10, 10};
        }else if (modulation.equals("BPSK")) {
            return new int[]{4, 4, 6, 6, 6, 8, 8, 10, 10, 10, 12, 12, 14, 14, 14, 16, 16, 18,
                    18, 18, 20, 20, 22, 22, 22, 24, 24, 26, 26, 26, 28, 28, 30, 30, 30, 32, 32, 34,
                    34, 34};
        }else if (modulation.equals("QAM8")) {
            return new int[]{4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 8, 8,
                    10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14};
        }else {
            return new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6,
                    6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
        }
    }
}
