package io.github.stscoundrel.nanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import javax.annotation.Nullable;

public class NanoId {

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.random() - Generate default length nanoId with default alphabet.")
    public String random() {
        return NanoIdUtils.randomNanoId();
    }

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.withSize(15) - Generate custom length nanoId with default alphabet.")
    public String withSize(
            @Name(value = "size", defaultValue = "21") @Nullable final Number size
    ) {
        if (size == null || size.intValue() <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0.");
        }

        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, size.intValue());
    }

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.withAlphabet('abc') - Generate default length nanoId with custom alphabet.")
    public String withAlphabet(
            @Name(value = "alphabet") @Nullable final String alphabet
    ) {
        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("Alphabet must not be empty.");
        }

        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet.toCharArray(), NanoIdUtils.DEFAULT_SIZE);
    }

    @UserFunction
    @Description("io.github.stscoundrel.nanoid.withAlphabetAndSize('abc', 5) - Generate custom length nanoId with custom alphabet.")
    public String withAlphabetAndSize(
            @Name(value = "alphabet") @Nullable String alphabet,
            @Name(value = "size", defaultValue = "21") @Nullable final Number size
    ) {
        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("Alphabet must not be empty.");
        }

        if (size == null || size.intValue() <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0.");
        }

        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet.toCharArray(), size.intValue());
    }
}