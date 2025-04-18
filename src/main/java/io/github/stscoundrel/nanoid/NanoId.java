package io.github.stscoundrel.nanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class NanoId {

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.random() - Generate default length nanoId with default alphabet.")
    public String random() {
        return NanoIdUtils.randomNanoId();
    }

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.withSize(15) - Generate custom length nanoId with default alphabet.")
    public String withSize(
            @Name(value = "size", defaultValue = "21") Number size
    ) {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, size.intValue());
    }

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.withAlphabet('abc') - Generate default length nanoId with custom alphabet.")
    public String withAlphabet(
            @Name(value = "alphabet") String alphabet
    ) {

        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet.toCharArray(), NanoIdUtils.DEFAULT_SIZE);
    }

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.withAlphabetAndSize('abc', 5) - Generate custom length nanoId with custom alphabet.")
    public String withAlphabetAndSize(
            @Name(value = "alphabet") String alphabet,
            @Name(value = "size", defaultValue = "21") Number size
    ) {

        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet.toCharArray(), size.intValue());
    }
}