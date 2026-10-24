const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read profiles or orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("profiles").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("orders").where("tenantKey", "==", "resolvitos_live").get());
});

test("Authenticated user: can read own profile but cannot read another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("profiles").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob",
      role: "client",
      activeRole: "client",
      points: 100,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("profiles").doc(BOB_UID).get());

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(bobDb.collection("profiles").doc(BOB_UID).get());
});

test("Authenticated user: can query comercios and business_missions with tenantKey filter", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("comercios").doc("store_1").set({
      tenantKey: "resolvitos_live",
      ownerId: ALICE_UID,
      name: "Arepera El Resolvito",
      category: "Restaurantes",
      city: "Caracas",
      deliveryFeeUsd: 1.5,
      isActive: true,
      isApproved: true,
    });
    await context.firestore().collection("business_missions").doc("mission_1").set({
      tenantKey: "resolvitos_live",
      comercioId: "store_1",
      comercioName: "Arepera El Resolvito",
      creatorId: ALICE_UID,
      title: "3 Pedidos = Bebida Gratis",
      targetPoints: 90,
      targetOrders: 3,
      rewardTitle: "Papelón con Limón Gratis",
      rewardValueUsd: 2.5,
      isActive: true,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("comercios").where("tenantKey", "==", "resolvitos_live").get()
  );
  await assertSucceeds(
    aliceDb.collection("business_missions").where("tenantKey", "==", "resolvitos_live").get()
  );
  // Query without tenantKey filter must fail
  await assertFails(aliceDb.collection("comercios").get());
});

test("Authenticated user: cannot read another user's mission claims", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("user_mission_claims").doc("claim_bob").set({
      userId: BOB_UID,
      missionId: "mission_1",
      comercioId: "store_1",
      rewardTitle: "Hamburguesa Gratis",
      couponCode: "RES-999",
      status: "unlocked",
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("user_mission_claims").doc("claim_bob").get());
  await assertFails(
    aliceDb.collection("user_mission_claims").where("userId", "==", BOB_UID).get()
  );
});

test("Authenticated user: can read/write own user_mission_progress but cannot access another user's progress", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("user_mission_progress").doc("ump_bob_mission_1").set({
      userId: BOB_UID,
      missionId: "mission_1",
      comercioId: "store_1",
      comercioName: "911 Grill",
      missionTitle: "Reto 911 Grill",
      currentPoints: 50,
      targetPoints: 100,
      currentOrders: 1,
      targetOrders: 2,
      pointsReward: 60,
      isCompleted: false,
      rewardClaimed: false,
      couponCode: "",
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertFails(aliceDb.collection("user_mission_progress").doc("ump_bob_mission_1").get());
  await assertFails(
    aliceDb.collection("user_mission_progress").where("userId", "==", BOB_UID).get()
  );
  await assertSucceeds(bobDb.collection("user_mission_progress").doc("ump_bob_mission_1").get());
  await assertSucceeds(
    bobDb.collection("user_mission_progress").where("userId", "==", BOB_UID).get()
  );
});

