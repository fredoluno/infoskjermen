# Dependency Upgrade TODO

This file contains the planned dependency upgrades for the Infoskjermen project.

## Current Status
- ✅ Spring Boot 3.0.13 → 3.4.1 upgrade complete
- ✅ Java 21 compatibility achieved  
- ✅ Application fully functional with all services operational
- ✅ Git workflow completed and deployed to master

## Pending Dependency Upgrades

### Phase 1: Easy Updates
- [ ] **Upgrade batik-codec to 1.17**
  - Current: 1.11
  - Target: 1.17 (to match batik-transcoder version)

- [ ] **Upgrade dotenv-java to 3.1.0**
  - Current: 3.0.0
  - Target: 3.1.0

- [ ] **Upgrade App Engine Maven plugin to 2.8.0**
  - Current: 1.3.2
  - Target: 2.8.0

### Phase 2: Google API Libraries
- [ ] **Upgrade google-api-client to 2.8.1**
  - Current: 1.30.2
  - Target: 2.8.1

- [ ] **Upgrade google-api-services-calendar to latest**
  - Current: v3-rev379-1.25.0
  - Target: v3-rev20251207-2.0.0 (released 15 days ago)

- [ ] **Upgrade google-auth-library-oauth2-http to 1.42.0**
  - Current: 0.16.2
  - Target: 1.42.0-rc1

### Phase 3: Major Upgrade (Most Complex)
- [ ] **Major upgrade: Google Cloud Firestore to 3.35.0**
  - Current: 0.81.0-beta (very old!)
  - Target: 3.35.0-rc1
  - ⚠️ This is a major version jump that will likely require code changes

### Phase 4: Final Testing
- [ ] **Test application after all upgrades**
  - Verify all services still work
  - Check authentication flows
  - Test Firestore connectivity
  - Run full application test suite

## Notes
- Consider doing upgrades in phases and testing after each phase
- The Firestore upgrade will be the most complex due to the major version jump
- Maven Wrapper 3.6.0 limits some tooling but is stable for current setup
- Current Java 21.0.7 LTS is excellent and doesn't need upgrading

## Upgrade Strategy with Git Workflow

### Branching Strategy for Upgrades
Each phase should be done in a separate feature branch to maintain clean history and enable easy rollbacks:

1. **Phase 1**: `upgrade/phase1-easy-updates`
2. **Phase 2**: `upgrade/phase2-google-apis` 
3. **Phase 3**: `upgrade/phase3-firestore-major`
4. **Phase 4**: `upgrade/phase4-final-testing`

### Workflow for Each Phase
```bash
# Start each phase from latest master
git checkout master
git pull origin master
git checkout -b upgrade/phase1-easy-updates

# Make changes, test thoroughly
./mvnw clean test
./mvnw spring-boot:run

# Commit changes
git add .
git commit -m "upgrade: Complete Phase 1 dependency updates

- Upgrade batik-codec to 1.17
- Upgrade dotenv-java to 3.1.0
- Upgrade App Engine Maven plugin to 2.8.0
- All tests passing"

# Push branch and merge after verification
git push -u origin upgrade/phase1-easy-updates
git checkout master
git merge upgrade/phase1-easy-updates
git push origin master

# Clean up
git branch -d upgrade/phase1-easy-updates
git push origin --delete upgrade/phase1-easy-updates
```

### Recommended Implementation Order
1. Start with Phase 1 (easy updates) to build confidence
2. Move to Phase 2 (Google API libraries) which should be straightforward
3. Save Phase 3 (Firestore) for last as it requires the most testing
4. Thoroughly test after each phase