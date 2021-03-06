/*--------------------------------------------------------------------------+
|                                                                          |
| Copyright 2008-2012 Technische Universitaet Muenchen                     |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+--------------------------------------------------------------------------*/

package edu.tum.cs.ccts.protobuf.embedded;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;

import edu.tum.cs.ccts.protobuf.embedded.TestProtos.AddressBook;
import edu.tum.cs.ccts.protobuf.embedded.TestProtos.Person;
import edu.tum.cs.ccts.protobuf.embedded.TestProtos.Person.Builder;
import edu.tum.cs.ccts.protobuf.embedded.TestProtos.PhoneType;

/**
 * A junit-class for testing the protobuf-c compiler.
 * 
 * @author nvpopa
 * @author wolfgang.schwitzer
 */
public class TestWireFormat {

	/** The number of tests. */
	private static final int NUMBER_OF_TESTS = 1000;

	/** The number of multiple tests. */
	private static final int NUMBER_OF_MULTIPLE_TESTS = 100;

	/** The directory in the workspace used for testing. */
	private final String testDir = System.getProperty("user.dir") + "/test";

	/**
	 * The in file from which the c-program unpacks the data and packs it again
	 * in the out file.
	 */
	private final File inFile = new File(testDir + "/Test.in");

	/**
	 * The out file from which the java-program unpacks the data and compares it
	 * to its initial unpackes values.
	 */
	private final File outFile = new File(testDir + "/Test.out");

	@Test
	public void testMultipleMessagesWithRepeated() throws Exception {
		Random rand = new Random(1989);
		List<Builder> lBuilder = new ArrayList<Builder>();
		int repeatedNr;
		for (int step = 0; step < NUMBER_OF_MULTIPLE_TESTS; ++step) {
			AddressBook.Builder address = AddressBook.newBuilder();
			address.setAddress(getRandomString(rand));
			address.setNumber(rand.nextInt(32000) - 16000);

			Person.Builder iniPersonBuilder = Person.newBuilder();
			iniPersonBuilder.setAb(address);
			iniPersonBuilder.setId(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setId64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setSid(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setUid(rand.nextInt(32000));
			iniPersonBuilder.setUid64(rand.nextLong());
			iniPersonBuilder.setFid32(rand.nextInt(32000));
			iniPersonBuilder.setFid64(rand.nextLong());
			iniPersonBuilder.setSfid32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSfid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setIq(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setIqd(rand.nextDouble() * 20000 - 10000);
			iniPersonBuilder.setEmail(rand.nextBoolean());
			iniPersonBuilder.setPhone(PhoneType.valueOf(rand.nextInt(3) + 0));

			iniPersonBuilder.setName1(getRandomString(rand));
			iniPersonBuilder.setName2(getRandomString(rand));
			iniPersonBuilder.setName3(getRandomString(rand));
			iniPersonBuilder.setName4(getRandomString(rand));
			iniPersonBuilder.setName5(getRandomString(rand));
			iniPersonBuilder.setName6(getRandomString(rand));
			iniPersonBuilder.setBname(getRandomByteString(rand));

			iniPersonBuilder.setOptBool(true);
			// iniPersonBuilder.setOptString(getRandomString(rand));
			// iniPersonBuilder.setOptBytes(getRandomByteString(rand));
			iniPersonBuilder.setOptDouble(rand.nextDouble() * 20000 - 10000);
			iniPersonBuilder.setOptFloat(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setOptEnum(PhoneType.valueOf(rand.nextInt(2) + 1));
			iniPersonBuilder.setOptFixed32(rand.nextInt(32000));
			iniPersonBuilder.setOptFixed64(rand.nextLong());
			iniPersonBuilder.setOptInt32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSFixed32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptSFixed64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSInt32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptSInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptUInt32(rand.nextInt(32000));
			iniPersonBuilder.setOptUInt64(rand.nextLong());
			iniPersonBuilder.setOptAb(address);

			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addStrAttr(getRandomString(rand));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addBAttr(getRandomByteString(rand));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addFloatAttr(rand.nextFloat() * 20000 - 10000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder
						.addDoubleAttr(rand.nextDouble() * 20000 - 10000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addIntAttr(rand.nextInt(32000) - 16000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addInt64Attr(rand.nextLong() - 10000000000L);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSintAttr(rand.nextInt(32000) - 16000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSintAttr64(rand.nextLong() - 10000000000L);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addUintAttr(rand.nextInt(32000));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addUintAttr64(rand.nextLong());
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addFintAttr(rand.nextInt(32000));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addFintAttr64(rand.nextLong());
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSfintAttr(rand.nextInt(32000) - 16000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSfintAttr64(rand.nextLong() - 10000000000L);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addBoolAttr(rand.nextBoolean());
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder
						.addEnumAttr(PhoneType.valueOf(rand.nextInt(3) + 0));
			}

			lBuilder.add(iniPersonBuilder);
		}
		executeTest(lBuilder, 0);
	}

	@Test
	public void testRepeatedInMessages() throws Exception {
		Random rand = new Random(1989);
		List<Builder> lBuilder = new ArrayList<Builder>();
		int repeatedNr;
		for (int step = 0; step < NUMBER_OF_TESTS; ++step) {
			lBuilder.clear();
			AddressBook.Builder address = AddressBook.newBuilder();
			address.setAddress(getRandomString(rand));
			address.setNumber(rand.nextInt(32000) - 16000);

			Person.Builder iniPersonBuilder = Person.newBuilder();
			iniPersonBuilder.setAb(address);
			iniPersonBuilder.setId(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setId64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setSid(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setUid(rand.nextInt(32000));
			iniPersonBuilder.setUid64(rand.nextLong());
			iniPersonBuilder.setFid32(rand.nextInt(32000));
			iniPersonBuilder.setFid64(rand.nextLong());
			iniPersonBuilder.setSfid32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSfid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setIq(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setIqd(rand.nextDouble() * 20000 - 10000);
			iniPersonBuilder.setEmail(rand.nextBoolean());
			iniPersonBuilder.setPhone(PhoneType.valueOf(rand.nextInt(3) + 0));

			iniPersonBuilder.setName1(getRandomString(rand));
			iniPersonBuilder.setName2(getRandomString(rand));
			iniPersonBuilder.setName3(getRandomString(rand));
			iniPersonBuilder.setName4(getRandomString(rand));
			iniPersonBuilder.setName5(getRandomString(rand));
			iniPersonBuilder.setName6(getRandomString(rand));
			iniPersonBuilder.setBname(getRandomByteString(rand));

			// iniPersonBuilder.setOptBool(true);
			iniPersonBuilder.setOptString(getRandomString(rand));
			iniPersonBuilder.setOptBytes(getRandomByteString(rand));
			iniPersonBuilder.setOptDouble(rand.nextDouble() * 20000 - 10000);
			iniPersonBuilder.setOptFloat(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setOptEnum(PhoneType.valueOf(rand.nextInt(2) + 1));
			iniPersonBuilder.setOptFixed32(rand.nextInt(32000));
			iniPersonBuilder.setOptFixed64(rand.nextLong());
			// iniPersonBuilder.setOptInt32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSFixed32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptSFixed64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSInt32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptSInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptUInt32(rand.nextInt(32000));
			iniPersonBuilder.setOptUInt64(rand.nextLong());
			iniPersonBuilder.setOptAb(address);

			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addStrAttr(getRandomString(rand));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addBAttr(getRandomByteString(rand));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addFloatAttr(rand.nextFloat() * 20000 - 10000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder
						.addDoubleAttr(rand.nextDouble() * 20000 - 10000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addIntAttr(rand.nextInt(32000) - 16000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addInt64Attr(rand.nextLong() - 10000000000L);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSintAttr(rand.nextInt(32000) - 16000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSintAttr64(rand.nextLong() - 10000000000L);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addUintAttr(rand.nextInt(32000));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addUintAttr64(rand.nextLong());
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addFintAttr(rand.nextInt(32000));
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addFintAttr64(rand.nextLong());
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSfintAttr(rand.nextInt(32000) - 16000);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addSfintAttr64(rand.nextLong() - 10000000000L);
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder.addBoolAttr(rand.nextBoolean());
			}
			repeatedNr = rand.nextInt(33);
			for (int i = 0; i < repeatedNr; ++i) {
				iniPersonBuilder
						.addEnumAttr(PhoneType.valueOf(rand.nextInt(3) + 0));
			}

			lBuilder.add(iniPersonBuilder);
			executeTest(lBuilder, 0);
		}
	}

	@Test
	public void testMultipleMessages() throws Exception {
		Random rand = new Random(1989);
		List<Builder> lBuilder = new ArrayList<Builder>();
		for (int step = 0; step < NUMBER_OF_MULTIPLE_TESTS; ++step) {
			AddressBook.Builder address = AddressBook.newBuilder();
			address.setAddress(getRandomString(rand));
			address.setNumber(rand.nextInt(32000) - 16000);

			Person.Builder iniPersonBuilder = Person.newBuilder();
			iniPersonBuilder.setAb(address);
			iniPersonBuilder.setId(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setId64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setSid(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setUid(rand.nextInt(32000));
			iniPersonBuilder.setUid64(rand.nextLong());
			iniPersonBuilder.setFid32(rand.nextInt(32000));
			iniPersonBuilder.setFid64(rand.nextLong());
			iniPersonBuilder.setSfid32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSfid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setIq(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setIqd(rand.nextDouble() * 20000 - 10000);
			iniPersonBuilder.setEmail(rand.nextBoolean());
			iniPersonBuilder.setPhone(PhoneType.valueOf(rand.nextInt(3) + 0));

			iniPersonBuilder.setName1(getRandomString(rand));
			iniPersonBuilder.setName2(getRandomString(rand));
			iniPersonBuilder.setName3(getRandomString(rand));
			iniPersonBuilder.setName4(getRandomString(rand));
			iniPersonBuilder.setName5(getRandomString(rand));
			iniPersonBuilder.setName6(getRandomString(rand));
			iniPersonBuilder.setBname(getRandomByteString(rand));

			iniPersonBuilder.setOptBool(true);
			iniPersonBuilder.setOptString(getRandomString(rand));
			iniPersonBuilder.setOptBytes(getRandomByteString(rand));
			// iniPersonBuilder.setOptDouble(rand.nextDouble() * 20000 - 10000);
			// iniPersonBuilder.setOptFloat(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setOptEnum(PhoneType.valueOf(rand.nextInt(2) + 1));
			iniPersonBuilder.setOptFixed32(rand.nextInt(32000));
			iniPersonBuilder.setOptFixed64(rand.nextLong());
			iniPersonBuilder.setOptInt32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSFixed32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptSFixed64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSInt32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setOptSInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptUInt32(rand.nextInt(32000));
			iniPersonBuilder.setOptUInt64(rand.nextLong());
			iniPersonBuilder.setOptAb(address);

			lBuilder.add(iniPersonBuilder);
		}
		executeTest(lBuilder, 0);
	}

	@Test
	public void testRandomInTheLoop() throws Exception {
		Random rand = new Random(1989);
		for (int step = 0; step < NUMBER_OF_TESTS; ++step) {
			AddressBook.Builder address = AddressBook.newBuilder();
			address.setAddress(getRandomString(rand));
			address.setNumber(rand.nextInt(32000) - 16000);

			Person.Builder iniPersonBuilder = Person.newBuilder();
			iniPersonBuilder.setAb(address);
			iniPersonBuilder.setId(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setId64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setSid(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setUid(rand.nextInt(32000));
			iniPersonBuilder.setUid64(rand.nextLong());
			iniPersonBuilder.setFid32(rand.nextInt(32000));
			iniPersonBuilder.setFid64(rand.nextLong());
			iniPersonBuilder.setSfid32(rand.nextInt(32000) - 16000);
			iniPersonBuilder.setSfid64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setIq(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setIqd(rand.nextDouble() * 20000 - 10000);
			iniPersonBuilder.setEmail(rand.nextBoolean());
			iniPersonBuilder.setPhone(PhoneType.valueOf(rand.nextInt(3) + 0));

			iniPersonBuilder.setName1(getRandomString(rand));
			iniPersonBuilder.setName2(getRandomString(rand));
			iniPersonBuilder.setName3(getRandomString(rand));
			iniPersonBuilder.setName4(getRandomString(rand));
			iniPersonBuilder.setName5(getRandomString(rand));
			iniPersonBuilder.setName6(getRandomString(rand));
			iniPersonBuilder.setBname(getRandomByteString(rand));

			iniPersonBuilder.setOptBool(true);
			iniPersonBuilder.setOptString(getRandomString(rand));
			iniPersonBuilder.setOptBytes(getRandomByteString(rand));
			iniPersonBuilder.setOptDouble(rand.nextDouble() * 20000 - 10000);
			iniPersonBuilder.setOptFloat(rand.nextFloat() * 20000 - 10000);
			iniPersonBuilder.setOptEnum(PhoneType.valueOf(rand.nextInt(2) + 1));
			iniPersonBuilder.setOptFixed32(rand.nextInt(32000));
			// iniPersonBuilder.setOptFixed64(rand.nextLong());
			iniPersonBuilder.setOptInt32(rand.nextInt(32000) - 16000);
			// iniPersonBuilder.setOptInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSFixed32(rand.nextInt(32000) - 16000);
			// iniPersonBuilder.setOptSFixed64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptSInt32(rand.nextInt(32000) - 16000);
			// iniPersonBuilder.setOptSInt64(rand.nextLong() - 10000000000L);
			iniPersonBuilder.setOptUInt32(rand.nextInt(32000));
			// iniPersonBuilder.setOptUInt64(rand.nextLong());
			iniPersonBuilder.setOptAb(address);

			List<Builder> lBuilder = new ArrayList<Builder>();
			lBuilder.add(iniPersonBuilder);
			executeTest(lBuilder, step);
		}
	}

	@Test
	public void testEquivalenceClassesLoop() throws FileNotFoundException,
			IOException {
		AddressBook.Builder address = AddressBook.newBuilder();
		address.setAddress("abcdefgh0123");

		List<String> stringClasses = new ArrayList<String>();
		stringClasses.add("");
		stringClasses.add("Hello");
		stringClasses.add("01234567890123456789012345678901");

		List<Integer> int32Classes = new ArrayList<Integer>();
		int32Classes.add(0);
		int32Classes.add(1);
		int32Classes.add(-1);
		int32Classes.add(Integer.MAX_VALUE);
		int32Classes.add(Integer.MIN_VALUE + 1);

		List<Float> floatClasses = new ArrayList<Float>();
		floatClasses.add(+0.0f);
		floatClasses.add(-0.0f);
		floatClasses.add(-1f);
		floatClasses.add(1f);

		List<Boolean> boolClasses = new ArrayList<Boolean>();
		boolClasses.add(true);
		boolClasses.add(false);

		int step = 0;
		for (String s : stringClasses) {
			for (String s2 : stringClasses) {
				for (Integer i : int32Classes) {
					for (Float f : floatClasses) {
						for (Boolean b : boolClasses) {
							Person.Builder iniPersonBuilder = Person
									.newBuilder();
							address.setNumber(i);
							iniPersonBuilder.setAb(address);
							iniPersonBuilder.setId(i);
							if (i >= Integer.MAX_VALUE) {
								iniPersonBuilder.setId64(Long.MAX_VALUE);
								iniPersonBuilder.setSid64(Long.MAX_VALUE);
								iniPersonBuilder.setUid64(Long.MAX_VALUE);
								iniPersonBuilder.setFid64(Long.MAX_VALUE);
								iniPersonBuilder.setSfid64(Long.MAX_VALUE);
							} else if (i <= Integer.MIN_VALUE + 1) {
								iniPersonBuilder.setId64(Long.MIN_VALUE);
								iniPersonBuilder.setSid64(Long.MIN_VALUE);
								iniPersonBuilder.setUid64(Long.MAX_VALUE);
								iniPersonBuilder.setFid64(Long.MAX_VALUE);
								iniPersonBuilder.setSfid64(Long.MIN_VALUE);
							} else {
								iniPersonBuilder.setId64(i);
								iniPersonBuilder.setSid64(i);
								iniPersonBuilder.setUid64(i);
								iniPersonBuilder.setFid64(i);
								iniPersonBuilder.setSfid64(i);
							}
							iniPersonBuilder.setSid(i);
							iniPersonBuilder.setSfid32(i);
							if (i < 0) {
								iniPersonBuilder.setUid(-i);
								iniPersonBuilder.setFid32(-i);
							} else {
								iniPersonBuilder.setUid(i);
								iniPersonBuilder.setFid32(i);
							}
							iniPersonBuilder.setIq(f);
							iniPersonBuilder.setIqd(f);
							iniPersonBuilder.setEmail(b);
							iniPersonBuilder.setPhone(PhoneType
									.valueOf(step % 3));

							iniPersonBuilder.setName1(s);
							iniPersonBuilder.setName2(s2);
							iniPersonBuilder.setName3(s);
							iniPersonBuilder.setName4(s2);
							iniPersonBuilder.setName5(s);
							iniPersonBuilder.setName6(s2);
							iniPersonBuilder.setBname(ByteString.copyFrom(s
									.getBytes("UTF-8")));
							List<Builder> lBuilder = new ArrayList<Builder>();
							lBuilder.add(iniPersonBuilder);
							executeTest(lBuilder, step++);
						}
					}
				}
			}
		}

	}

	private void executeTest(List<Builder> lBuilder, int step)
			throws FileNotFoundException, IOException {
		FileOutputStream output = new FileOutputStream(inFile.getAbsolutePath());
		List<Person> lPerson = new ArrayList<Person>();
		for (Builder builder : lBuilder) {
			lPerson.add(builder.build());
		}
		for (Person person : lPerson) {
			person.writeDelimitedTo(output);
		}
		output.close();

		ProcessBuilder cProcess = null;
		FileInputStream testData = null;

		if (System.getProperty("os.name").equals("Mac OS X")) {
			cProcess = new ProcessBuilder("./Test");
			cProcess.directory(new File(testDir));
			testData = new FileInputStream(new File(testDir
					+ "/Test.properties"));
		} else if (System.getProperty("os.name").startsWith("Windows")) {
			cProcess = new ProcessBuilder("cmd", "/C", "Test.exe");
			cProcess.directory(new File(testDir));
			testData = new FileInputStream(new File(testDir
					+ "\\Test.properties"));
		}

		Process process = cProcess.start();
		InputStream inputStream = process.getInputStream();

		while (inputStream.read() >= 0) {
			// Do nothing
		}

		FileInputStream in = new FileInputStream(outFile);

		Properties p = new Properties();
		p.load(testData);

		Person person, iniPerson = null;
		try {
			// Test wire format.
			step = 0;
			while ((person = Person.parseDelimitedFrom(in)) != null) {

				// Test j_pack-c_unpack-c_pack-j_unpack-js cycle.
				iniPerson = lPerson.get(step++);
				checkOptionalAttributes(person, iniPerson);
				checkRequiredRepeatedAttributes(person, iniPerson);

				// Test whether c program sees the same values as java.
				if (lBuilder.size() == 1) {
					Assert.assertEquals(iniPerson.getId(),
							parseInt(p.getProperty("id")));
					Assert.assertEquals(iniPerson.getId64(),
							parseLong(p.getProperty("id64")));
					Assert.assertEquals(iniPerson.getSid(),
							parseInt(p.getProperty("sid")));
					Assert.assertEquals(iniPerson.getSid64(),
							parseLong(p.getProperty("sid64")));
					Assert.assertEquals(iniPerson.getUid(),
							parseInt(p.getProperty("uid")));
					Assert.assertEquals(iniPerson.getUid64(),
							parseLong(p.getProperty("uid64")));
					Assert.assertEquals(iniPerson.getFid32(),
							parseInt(p.getProperty("fid32")));
					Assert.assertEquals(iniPerson.getFid64(),
							parseLong(p.getProperty("fid64")));
					// Assert.assertEquals(iniPerson.getSfid32(),
					// parseInt(p.getProperty("sfid32")));
					Assert.assertEquals(iniPerson.getSfid64(),
							parseLong(p.getProperty("sfid64")));
					Assert.assertEquals(limitFloat(iniPerson.getIq()),
							limitFloat(Float.parseFloat(p.getProperty("iq"))));
					Assert.assertTrue(abs(iniPerson.getIqd()
							- Double.parseDouble(p.getProperty("iqd"))) < 10e-10);
					Assert.assertEquals(iniPerson.getEmail(), parseInt(p
							.getProperty("email")) == 1 ? true : false);
					Assert.assertEquals(iniPerson.getPhone().ordinal(),
							parseInt(p.getProperty("phonetype")));
					Assert.assertEquals(iniPerson.getName1(),
							p.getProperty("name1"));
					Assert.assertEquals(iniPerson.getName2(),
							p.getProperty("name2"));
					Assert.assertEquals(iniPerson.getName3(),
							p.getProperty("name3"));
					Assert.assertEquals(iniPerson.getName4(),
							p.getProperty("name4"));
					Assert.assertEquals(iniPerson.getName5(),
							p.getProperty("name5"));
					Assert.assertEquals(iniPerson.getName6(),
							p.getProperty("name6"));
					Assert.assertEquals(iniPerson.getAb().getAddress(),
							p.getProperty("abaddress"));
					Assert.assertEquals(iniPerson.getAb().getNumber(),
							parseInt(p.getProperty("abnumber")));
					Assert.assertEquals(iniPerson.getBname().toStringUtf8(),
							p.getProperty("bname"));
				}
			}
		} catch (Throwable t) {
			System.err.println();
			System.err.println("ERROR REPORT");
			System.err.println("Failed in step " + step);
			System.err.println(t.getMessage());
			System.err.println("Person:");
			System.err.println(iniPerson);
			System.err.println("Properties:");
			System.err.println(p);
			System.err.println("In/out file:");
			printFile(inFile);
			printFile(outFile);
			System.err.println();
		}

		in.close();
		testData.close();
	}

	private void checkRequiredRepeatedAttributes(Person person, Person iniPerson) {
		for (Entry<FieldDescriptor, Object> e : iniPerson.getAllFields()
				.entrySet()) {
			if (e.getKey().isOptional()) {
				continue;
			}
			boolean foundAttribute = false;
			for (Entry<FieldDescriptor, Object> o : person.getAllFields()
					.entrySet()) {
				if (e.equals(o)) {
					foundAttribute = true;
					break;
				}
			}
			Assert.assertTrue(foundAttribute);
		}

		Assert.assertEquals(person.getUnknownFields(),
				iniPerson.getUnknownFields());
	}

	private void checkOptionalAttributes(Person person, Person iniPerson) {
		if (iniPerson.getOptBool() != person.getOptBool()
				&& person.hasOptBool()) {
			Assert.assertTrue(false);
		}
		if (!iniPerson.getOptAb().equals(person.getOptAb())
				&& person.hasOptAb()) {
			Assert.assertTrue(false);
		}
		if (!iniPerson.getOptBytes().equals(person.getOptBytes())
				&& person.hasOptBytes()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptDouble() != person.getOptDouble()
				&& person.hasOptDouble()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptEnum() != person.getOptEnum()
				&& person.hasOptEnum()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptFixed32() != person.getOptFixed32()
				&& person.hasOptFixed32()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptFixed64() != person.getOptFixed64()
				&& person.hasOptFixed64()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptFloat() != person.getOptFloat()
				&& person.hasOptFloat()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptInt32() != person.getOptInt32()
				&& person.hasOptInt32()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptInt64() != person.getOptInt64()
				&& person.hasOptInt64()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptSFixed32() != person.getOptSFixed32()
				&& person.hasOptSFixed32()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptSFixed64() != person.getOptSFixed64()
				&& person.hasOptSFixed64()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptSInt32() != person.getOptSInt32()
				&& person.hasOptSInt32()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptSInt64() != person.getOptSInt64()
				&& person.hasOptSInt64()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptUInt32() != person.getOptUInt32()
				&& person.hasOptUInt32()) {
			Assert.assertTrue(false);
		}
		if (iniPerson.getOptUInt64() != person.getOptUInt64()
				&& person.hasOptUInt64()) {
			Assert.assertTrue(false);
		}
		if (!iniPerson.getOptString().equals(person.getOptString())
				&& person.hasOptString()) {
			Assert.assertTrue(false);
		}
	}

	/**
	 * Prints the file on standard error console.
	 */
	private void printFile(File file) throws IOException {
		FileInputStream content = new FileInputStream(file);
		int chr;
		while ((chr = content.read()) >= 0) {
			String s = String.valueOf(chr);
			while (s.length() < 3) {
				s = 0 + s;
			}
			System.err.print(s + '|');
		}
		System.err.println();
		content.close();
	}

	/**
	 * Returns a random string.
	 */
	private String getRandomString(Random rand) {
		String name = "";
		int size = rand.nextInt(32) + 1;
		for (int i = 0; i < size; ++i) {
			name += (char) (rand.nextInt(26) + 64);
		}
		return name;
	}

	/**
	 * Returns a random byte string.
	 */
	private ByteString getRandomByteString(Random rand) {
		int size = rand.nextInt(32) + 1;
		byte[] name = new byte[size];
		for (int i = 0; i < size; ++i) {
			name[i] += rand.nextInt(26) + 100;
		}
		return ByteString.copyFrom(name);
	}

	/**
	 * Returns as a string the float with a precision of 10e-7.
	 */
	private String limitFloat(float f) {
		String s = String.valueOf(f);
		if (s.length() > 7) {
			s = s.substring(0, 7);
		}
		return s;
	}
}
