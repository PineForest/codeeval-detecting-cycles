/*
 * Copyright © 2016  David Williams
 *
 * This file is part of the codeeval-detecting-cycles project.
 *
 * codeeval-detecting-cycles is free software: you can redistribute it and/or modify it under the terms of the
 * Lesser GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * codeeval-detecting-cycles is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public
 * License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with codeeval-detecting-cycles.
 * If not, see <a href="http://www.gnu.org/licenses/">www.gnu.org/licenses/</a>.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * <p>The "Detecting Cycles" code challenge found at <a href="https://www.codeeval.com/public_sc/5/">CodeEval</a>.
 * </p>
 *
 * Some thoughts:
 * <ul>
 *     <li>I assumed that the goal is to demonstrate the implementation of the fastest algorithm for finding the result,
 *     not to demonstrate the fastest speed to read and process large files. Due to this, the implementation is
 *     performance constrained by disk IO and string tokenization.</li>
 *     <li>I assumed that user friendly error messages, comments, and test code are a non-goal, though I did include a
 *     couple of these. Specifically, I assumed, based on the sample input, that a) each input line had a cycle, b) the
 *     cycle is repeated at least twice, c) the cycle and its duplicates are in an uninterrupted sequence, and d) the
 *     last duplicate terminates the input line (ie there are no additional elements after the final duplicate cycle).
 *     </li>
 *     <li>The code challenge references <a href="https://en.wikipedia.org/wiki/Cycle_detection">WikiPedia</a> and its
 *     discussions on algorithms used in solving this in O(λ + μ) time and O(1) space. These algorithms assume the cycle
 *     is described by the links found within a linked data structure. I considered three approaches to this.
 *         <ol>
 *             <li>I examined converting the input into a linked list data structure and then using a well known
 *             algorithm, like Floyd's, to find the cycle. This approach would require parsing the input line one
 *             element at a time and constructing a linked list from the elements (O(n) for time and space) before
 *             applying Floyd's algorithm. Time: O(n + λ + μ), Space: O(n + 1). NOTE: O(n + λ + μ) is descriptive in
 *             nature and for practical purposes is the same as O(n).</li>
 *             <li>I examined converting the input into a linked list data structure (just as described in bullet 1).
 *             Instead of converting the entire input, I would convert one element at a time and look for the first
 *             occurrence of a repeat value. In order to find a repeat, an additional {@link HashMap} would be used to
 *             hold each element's value and index in the linked list (O(n) for time and space). Time: O(n + λ + μ),
 *             Space: O(n). NOTE: O(n + λ + μ) is descriptive in nature and for practical purposes is the same as O(n).
 *             </li>
 *             <li>I had an insight which informed my solution below. I noticed that once a cycle occurred, it would be
 *             repeated one or more times without interruption until the end of the input line. Additionally, I
 *             recognized that if the elements in the input line were placed into a list and the list was reversed, the
 *             cycle elements would be reversed, the reversed cycle elements will continue to be a cycle, the first
 *             element of the reversed cycle would be the first element in the list, and the reversed cycle will repeat
 *             itself one or more times without interruption before reaching non-repeating elements or the end of the
 *             list. To find the complete (reversed) cycle, it is necessary to locate the first duplicate of the first
 *             element in list. When the first duplicate is found, you have located the start of a repeat of the
 *             (reversed) cycle. The (actual) cycle is described by traversing the list in reverse order, starting with
 *             the element before the first duplicate element in the list and ending at the first element in the list.
 *             As an added bonus, assuming valid input, approximately half of the input line needs to be parsed. This is
 *             because the cycle will either repeat itself within the first half of the total number of elements in the
 *             input line or it will repeat itself starting at the first element in the second half of the list. This
 *             algorithm is at worst O(n/2) for time and space. NOTE: in Big-O notation, O(n/2) is descriptive in nature
 *             and for practical purposes is the same as O(n).</li>
 *         </ol>
 *     </li>
 * </ul>
 *
 * @author <a href="https://github.com/PineForest">PineForest</a> 10/8/2015
 */
public class Main {
    private static void printCycle(String line) {
        StringBuilder builder = new StringBuilder();
        LinkedList<String> elements = new LinkedList<>();
        for (int i = line.length() - 1 ; i >= 0 ; --i) {
            if (line.charAt(i) != ' ') {
                builder.insert(0, line.charAt(i));
                if (i > 0) {
                    continue;
                }
            }
            if (builder.length() == 0) {
                continue;
            }
            String value = builder.toString();
            builder.delete(0, builder.length());
            if (value.equals(elements.peekLast())) {
                break;
            }
            elements.addFirst(value);
        }
        ListIterator<String> elementsIter = elements.listIterator();
        while (elementsIter.hasNext()) {
            if (elementsIter.nextIndex() != 0) {
                System.out.print(" ");
            }
            System.out.print(elementsIter.next());
        }
        System.out.println();
    }

    public static void main (String[] args) throws IOException {
        File file = new File(args[0]);
        BufferedReader buffer = new BufferedReader(new FileReader(file));
        String line;
        while ((line = buffer.readLine()) != null) {
            line = line.trim();
            printCycle(line);
        }
    }
}
