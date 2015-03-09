package org.apache.jackrabbit.oak.plugins.index.lucene.util;

import org.apache.jackrabbit.oak.plugins.index.lucene.FieldNames;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.DirectSpellChecker;
import org.apache.lucene.search.spell.SuggestWord;

/**
 * Helper class for getting spellcheck results for a given term, calling a {@link org.apache.lucene.search.spell.DirectSpellChecker}
 * under the hood.
 */
public class SpellcheckHelper {
    public static SuggestWord[] getSpellcheck(SpellcheckQuery spellcheckQuery) {
        try {
            DirectSpellChecker spellChecker = new DirectSpellChecker();
            return spellChecker.suggestSimilar(spellcheckQuery.getTerm(), spellcheckQuery.getCount(), spellcheckQuery.getReader());
        } catch (Exception e) {
            throw new RuntimeException("could not handle Spellcheck query " + spellcheckQuery, e);
        }
    }

    public static SpellcheckQuery getSpellcheckQuery(String spellcheckQueryString, IndexReader reader) {
        String text = null;
        for (String param : spellcheckQueryString.split("&")) {
            String[] keyValuePair = param.split("=");
            if (keyValuePair.length != 2 || keyValuePair[0] == null || keyValuePair[1] == null) {
                throw new RuntimeException("Unparsable native Lucene Spellcheck query: " + spellcheckQueryString);
            } else {
                if ("term".equals(keyValuePair[0])) {
                    text = keyValuePair[1];
                }
            }
        }
        return new SpellcheckHelper.SpellcheckQuery(new Term(FieldNames.SPELLCHECK, text), 10, reader);
    }

    public static class SpellcheckQuery {
        private final Term term;
        private final int count;
        private final IndexReader reader;

        public SpellcheckQuery(Term term, int count, IndexReader reader) {
            this.term = term;
            this.count = count;
            this.reader = reader;
        }

        public Term getTerm() {
            return term;
        }

        public int getCount() {
            return count;
        }

        public IndexReader getReader() {
            return reader;
        }

        @Override
        public String toString() {
            return "SpellcheckQuery{" +
                    "term=" + term +
                    ", count=" + count +
                    '}';
        }
    }
}
