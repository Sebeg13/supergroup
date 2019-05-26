import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class Finder {
    private int numberOfPeople;

    void run() {
        File data = new File("data.txt");
        HashMap<String, Person> peopleMap = convertFileToPeople(data);
        ArrayList<HashSet<Person>> friendsSets = createSets(peopleMap);
        HashSet<HashSet<Person>> superGroups = lookForSuperGroups(friendsSets);

        if (superGroups.size() > 1) {

            //convert HashSet to ArrayList
            ArrayList<HashSet<Person>> superGroupsArrList = new ArrayList<HashSet<Person>>(superGroups);

            ArrayList<HashSet<Person>> superGroupsWithTheMostOtherFriends = findGroupsWithTheMostOtherFriends(superGroupsArrList);


            if (superGroupsWithTheMostOtherFriends.size() == 1) {
                ArrayList<Person> superGroup = new ArrayList<Person>(superGroupsWithTheMostOtherFriends.get(0));
                printSuperGroup(sortGroup(superGroup));
            } else {
                ArrayList<ArrayList<Person>> sortedSuperGroups = new ArrayList<ArrayList<Person>>(superGroupsWithTheMostOtherFriends.size());

                // convert SuperGroups from HashSets to ArrayLists and sort them
                for (HashSet<Person> set : superGroupsWithTheMostOtherFriends) {
                    sortedSuperGroups.add(sortGroup(new ArrayList<Person>(set)));
                }

                printSuperGroup(findTheBestListAlphab(sortedSuperGroups));
            }

        } else {
            //there is only one superGroup in superGroups
            for (HashSet<Person> superGroup : superGroups) {
                printSuperGroup(sortGroup(new ArrayList<Person>(superGroup)));
            }
        }
    }

    private ArrayList<HashSet<Person>> findGroupsWithTheMostOtherFriends(ArrayList<HashSet<Person>> superGroupsArrList) {
        ArrayList<HashSet<Person>> groupsWithTheMostOtherFriends = new ArrayList<HashSet<Person>>();
        int biggestAmountOfOtherFriends = 0;

        for (HashSet<Person> superGroup : superGroupsArrList) {
            int amountOfOtherFriends = countAmountOfOtherFriends(superGroup);
            if (amountOfOtherFriends == biggestAmountOfOtherFriends) {
                groupsWithTheMostOtherFriends.add(superGroup);

            }
            if (amountOfOtherFriends > biggestAmountOfOtherFriends) {
                groupsWithTheMostOtherFriends.clear();
                biggestAmountOfOtherFriends = amountOfOtherFriends;
                groupsWithTheMostOtherFriends.add(superGroup);
            }
        }

        return groupsWithTheMostOtherFriends;
    }

    private void printSuperGroup(ArrayList<Person> superGroup) {
        System.out.println(superGroup.size());
        for (Person person : superGroup) {
            System.out.print(person + " ");
        }
    }

    private ArrayList<Person> findTheBestListAlphab(ArrayList<ArrayList<Person>> sortedSuperGroups) {
        ArrayList<Person> theBestSuperGroup = sortedSuperGroups.get(0);
        boolean superGroupChanged;
        do {
            superGroupChanged = false;
            for (ArrayList<Person> group : sortedSuperGroups) {
                if (group.equals(theBestSuperGroup))
                    continue;
                ArrayList<Person> newTheBestSuperGroup = compareNames(theBestSuperGroup, group);
                if (!newTheBestSuperGroup.equals(theBestSuperGroup)) {
                    theBestSuperGroup = newTheBestSuperGroup;
                    superGroupChanged = true;
                }
            }
        } while (superGroupChanged);
        return theBestSuperGroup;
    }

    private ArrayList<Person> compareNames(ArrayList<Person> groupA, ArrayList<Person> groupB) {
        for (int ii = 0; ii < groupA.size(); ii++) {

            int compareResult = groupA.get(ii).compareTo(groupB.get(ii));
            if (compareResult >= 1)
                return groupB;
            if (compareResult <= -1)
                return groupA;

        }
        return groupA;
    }

    private ArrayList<Person> sortGroup(ArrayList<Person> group) {
        Collections.sort(group);
        return group;
    }

    private HashSet<HashSet<Person>> lookForSuperGroups( ArrayList<HashSet<Person>> friendsSets) {
        HashSet<HashSet<Person>> superGroups = new HashSet<HashSet<Person>>();
        int biggestSuperGroupSize = 0;

        for (int ii = 0; ii < numberOfPeople; ii++) {
            for (int jj = ii + 1; jj < numberOfPeople; jj++) {
                HashSet<Person> superGroup = new HashSet<Person>(friendsSets.get(ii));
                superGroup.retainAll(friendsSets.get(jj));
                if (checkIfSuperGroup(superGroup)) {
                    if (superGroup.size() == biggestSuperGroupSize) {
                        superGroups.add(superGroup);
                    }
                    if (superGroup.size() > biggestSuperGroupSize) {
                        superGroups.clear();
                        superGroups.add(superGroup);
                        biggestSuperGroupSize = superGroup.size();
                    }

                }
            }
        }
        return superGroups;
    }


    private int countAmountOfOtherFriends(HashSet<Person> superGroup) {
        int result = 0;
        for (Person person : superGroup) {
            for (Person personsFriend : person.getFriends()) {
                if (!superGroup.contains(personsFriend))
                    result++;
            }
        }
        return result;
    }

    private boolean checkIfSuperGroup(HashSet<Person> superGroup) {

        for (Person person : superGroup) {
            for (Person personToCheck : superGroup) {
                if (!person.getFriends().contains(personToCheck) && !person.equals(personToCheck))
                    return false;
            }
        }
        return true;
    }

    private ArrayList<HashSet<Person>> createSets(HashMap<String, Person> peopleMap) {
        ArrayList<HashSet<Person>> friendsSets = new ArrayList<HashSet<Person>>(numberOfPeople);
        for (Person person : peopleMap.values()) {
            HashSet<Person> friendsSet = person.getFriends();
            friendsSet.add(person);
            friendsSets.add(friendsSet);
        }
        return friendsSets;
    }

    private HashMap<String, Person> convertFileToPeople(File data) {
        HashMap<String, Person> peopleMap = new HashMap<String, Person>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(data));
            String firstLine = br.readLine();
            String[] firstLineArr = firstLine.split(" ");
            numberOfPeople = Integer.valueOf(firstLineArr[0]);
            int numberOfRelations = Integer.valueOf(firstLineArr[1]);

            for (int ii = 0; ii < numberOfRelations; ii++) {
                String line = br.readLine();
                String[] lineArr = line.split(" ");
                String firstPersonString = lineArr[0];
                String secondPersonString = lineArr[1];
                Person firstPerson;
                Person secondPerson;

                if (peopleMap.containsKey(firstPersonString)) {
                    firstPerson = peopleMap.get(firstPersonString);

                } else {
                    firstPerson = new Person(firstPersonString);
                    peopleMap.put(firstPersonString, firstPerson);
                }

                if (peopleMap.containsKey(secondPersonString)) {
                    secondPerson = peopleMap.get(secondPersonString);

                } else {
                    secondPerson = new Person(secondPersonString);
                    peopleMap.put(secondPersonString, secondPerson);
                }

                firstPerson.addFriend(secondPerson);
                secondPerson.addFriend(firstPerson);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return peopleMap;
    }


}
